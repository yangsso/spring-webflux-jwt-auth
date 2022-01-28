package com.sonny.auth.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.sonny.auth.constant.AuthConstant
import com.sonny.auth.entity.KeyEntity
import com.sonny.auth.entity.MemberEntity
import com.sonny.auth.model.AuthToken
import com.sonny.auth.utils.decodeBase64
import com.sonny.auth.utils.toPrivateKey
import com.sonny.auth.utils.toPublicKey
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.Jwts
import org.jasypt.encryption.StringEncryptor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.nio.charset.StandardCharsets
import java.security.PrivateKey
import java.security.PublicKey
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.HashMap

@Service
class TokenService(
    private val authenticationService: AuthenticationService,
    private val memberService: MemberService,
    private val keyService: KeyService,
    private val redisTemplateService: RedisTemplateService,
    private val jasyptStringEncryptor: StringEncryptor
) {

    @Value("\${expiration.minute.accesstoken}")
    private val accessTokenExpirationMin: Long = 1440

    @Value("\${expiration.minute.refreshtoken}")
    private val refreshTokenExpirationMin: Long = 43200

    private val objectMapper = ObjectMapper().registerKotlinModule()

    companion object {
        private val logger = LoggerFactory.getLogger(TokenService::class.java)
        private const val BEARER = "Bearer "
        private const val REFRESH_TOKEN_KEY = "token:"
    }

    private fun getRedisRefreshTokenKey(accessTokenId: String) = "$REFRESH_TOKEN_KEY:$accessTokenId"
    private fun getBearerRemoveToken(token: String) = token.replaceFirst(BEARER, "")

    private fun extractKeyId(token: String) : String? {
        val splitToken = token.split('.')
        val header = objectMapper.readValue(String(Base64.getDecoder().decode(splitToken[0]), StandardCharsets.UTF_8), HashMap<String, String>()::class.java)
        return header[JwsHeader.KEY_ID]
    }

    fun validate(accessToken: String) {
        val token = getBearerRemoveToken(accessToken)
        val kid = extractKeyId(token) ?: throw IllegalArgumentException()
        keyService.findByKid(kid)
            .switchIfEmpty(Mono.error(NoSuchElementException()))
            .doOnNext {
                val publicKey = it.publicKey.decodeBase64().toPublicKey()
                val claims = Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).body
                memberService.findMemberByLoginId(claims.audience)
            }.doOnError {
                logger.error(it.message)
                throw it
            }.subscribe()
    }

    fun refreshToken(refreshToken: String): Mono<AuthToken> {
        val kid = extractKeyId(refreshToken) ?: throw IllegalArgumentException()
        val keyEntity = keyService.findByKid(kid).switchIfEmpty(Mono.error(NoSuchElementException()))
        val claims = keyEntity.map {
                val publicKey = it.publicKey.decodeBase64().toPublicKey()
                Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(refreshToken).body
            }

        val memberEntity = claims
            .doOnNext { validateRefreshToken(it) }
            .flatMap { memberService.findMemberByLoginId(it.audience)
        }

        return Mono.zip(memberEntity, keyEntity, claims.map { it.subject }).flatMap { generateToken(it.t1, it.t2, it.t3)}
    }

    fun validateRefreshToken(claims: Claims): Mono<Void> {
        val accessTokenId = claims[AuthConstant.CLAIM_VALUE_ACCESS_TOKEN_UUID] as String
        return redisTemplateService.get(getRedisRefreshTokenKey(accessTokenId))
            .switchIfEmpty { Mono.just("") }
            .cast(String::class.java)
            .doOnNext {
                if (it != claims[AuthConstant.CLAIM_VALUE_REFRESH_TOKEN_UUID]) {
                    error(IllegalArgumentException())
                }
            }.then()
    }

    fun create(loginId: String, service: String, authCode: String): Mono<AuthToken> {
        val member = memberService.findMemberByLoginId(loginId)
            .doOnNext { authenticationService.validateAuthCode(it.id, authCode)}

        val key = keyService.findByService(service)

        return Mono.zip(member, key).flatMap{ generateToken(it.t1, it.t2, service) }
    }

    fun generateToken(memberEntity: MemberEntity, keyEntity: KeyEntity, service: String): Mono<AuthToken> {
        val now = LocalDateTime.now()

        val accessTokenId = UUID.randomUUID()
        val refreshTokenId = UUID.randomUUID()

        val jwtBuilder = Jwts.builder()
            .setHeaderParam(JwsHeader.KEY_ID, keyEntity.kid)
            .setHeaderParam(JwsHeader.TYPE, "JWT")
            .setSubject(service)
            .setIssuer(AuthConstant.ISSUER)
            .setAudience(memberEntity.loginId)
            .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(getDecodedPrivateKey(keyEntity.privateKey))

        val accessToken = jwtBuilder
            .claim(AuthConstant.CLAIM_VALUE_ACCESS_TOKEN_UUID, accessTokenId)
            .claim(AuthConstant.CLAIM_PRIVATE_NAME_AUTHORITIES, "ROLE_MEMBER")
            .claim(AuthConstant.CLAIM_PRIVATE_NAME_TOKEN_TYPE, AuthConstant.CLAIM_VALUE_TOKEN_TYPE_ACCESS_TOKEN)
            .setExpiration(Date.from(now.plusMinutes(accessTokenExpirationMin).atZone(ZoneId.systemDefault()).toInstant()))
            .compact()

        val refreshToken = jwtBuilder
            .claim(AuthConstant.CLAIM_VALUE_ACCESS_TOKEN_UUID, accessTokenId)
            .claim(AuthConstant.CLAIM_VALUE_REFRESH_TOKEN_UUID, refreshTokenId)
            .setExpiration(Date.from(now.plusMinutes(refreshTokenExpirationMin).atZone(ZoneId.systemDefault()).toInstant()))
            .compact()

        return redisTemplateService.set(getRedisRefreshTokenKey(accessTokenId.toString()), refreshTokenId.toString(), Duration.ofMinutes(refreshTokenExpirationMin))
            .onErrorMap { error(RuntimeException()) }
            .map{
                AuthToken(
                    accessToken = accessToken,
                    expiresIn = accessTokenExpirationMin * 3600,
                    refreshToken = refreshToken,
                    refreshExpiresIn = refreshTokenExpirationMin * 3600,
                    tokenType = "Bearer"
                )
            }
    }

    private fun getDecodedPrivateKey(privateKey: String): PrivateKey {
        val decryptedKey = jasyptStringEncryptor.decrypt(privateKey)
        return Base64.getDecoder().decode(decryptedKey).toPrivateKey()
    }

}
