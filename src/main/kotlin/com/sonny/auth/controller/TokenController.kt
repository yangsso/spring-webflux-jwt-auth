package com.sonny.auth.controller

import com.sonny.auth.model.AuthToken
import com.sonny.auth.service.TokenService
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/token")
class TokenController(private val tokenService: TokenService) {

    @PostMapping("/{service}/member/{loginId}/{authCode}")
    fun generateToken(@PathVariable service: String,
                      @PathVariable loginId: String,
                      @PathVariable authCode: String): Mono<AuthToken> {
        return tokenService.create(loginId, service, authCode)
    }

    @PostMapping("/validate")
    fun validate(@RequestHeader(HttpHeaders.AUTHORIZATION) token: String) {
        tokenService.validate(token)
    }

    @PostMapping("/refresh")
    fun refreshToken(token: String): Mono<AuthToken> {
        return tokenService.refreshToken(token)
    }
}
