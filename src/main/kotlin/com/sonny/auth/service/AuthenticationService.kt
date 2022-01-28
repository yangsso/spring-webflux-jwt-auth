package com.sonny.auth.service

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.lang.IllegalArgumentException
import java.time.Duration

@Service
class AuthenticationService (
    private val memberService: MemberService,
    private val redisTemplateService: RedisTemplateService
){

    companion object {
        private const val AUTH_CODE_CAPACITY = 5
    }

    private fun authKey(code: String) = "code:$code"
    private fun authCountKey(memberId: Long) = "code:$memberId"

    fun authenticate(loginId: String, password: String) : Mono<String> {
        return memberService.findMemberByLoginModel(loginId, password)
            .switchIfEmpty(Mono.error(NoSuchElementException()))
            .flatMap{generateAuthorizationCode(it.id)}
    }

    fun generateAuthorizationCode(memberId: Long): Mono<String> {
        val code = RandomStringUtils.randomAlphanumeric(20)
        return redisTemplateService.set(authKey(code), memberId.toString(), Duration.ofMinutes(10))
            .doOnNext{isSet -> if (!isSet) {error("fail generate authCode")}}
            .then(Mono.just(code))
    }

    fun validateAuthCode(memberId: Long, authCode: String): Mono<Void> {
        return redisTemplateService.get(authKey(authCode))
            .switchIfEmpty(Mono.error(NoSuchElementException()))
            .map{
                if (memberId.toString() != it) {
                    error(IllegalArgumentException())
                }
            }
            .then()
    }

}
