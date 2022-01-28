package com.sonny.auth.service

import com.sonny.auth.entity.KeyEntity
import com.sonny.auth.enum.KeyType
import com.sonny.auth.enum.UseType
import com.sonny.auth.repository.JsonWebKeyRepository
import com.sonny.auth.utils.encodeBase64
import org.jasypt.encryption.StringEncryptor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.util.*

@Service
class KeyService(
    private val keyEntityService: KeyEntityService,
    private val jasyptStringEncryptor: StringEncryptor
) {

    companion object {
        private val logger = LoggerFactory.getLogger(KeyService::class.java)
    }

    fun generateKey(service: String) : Mono<String> {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048, SecureRandom())

        val keyPair = keyPairGenerator.genKeyPair()
        val keyEntity = KeyEntity(
            UUID.randomUUID().toString(),
            service,
            KeyType.RSA.name,
            UseType.SIG.name,
            "RS256",
            publicKey = keyPair.public.encodeBase64(),
            privateKey = jasyptStringEncryptor.encrypt(keyPair.private.encodeBase64())
        )
        return keyEntityService.add(keyEntity)
            .doOnError {
                logger.error(it.message)
                throw it
            }.map(KeyEntity::kid)
    }

    fun findByKid(kid: String) : Mono<KeyEntity> {
        return keyEntityService.findByKid(kid)
    }

    fun findByService(service: String): Mono<KeyEntity> {
        return keyEntityService.findByService(service)
    }

}
