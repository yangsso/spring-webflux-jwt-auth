package com.sonny.auth.controller

import com.sonny.auth.model.LoginModel
import com.sonny.auth.service.AuthenticationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthenticationController(
    private val authenticationService: AuthenticationService
) {

    @PostMapping("/member")
    fun authenticate(@RequestBody loginModel: LoginModel): Mono<String> {
        return authenticationService.authenticate(loginModel.loginId, loginModel.password)
    }
}
