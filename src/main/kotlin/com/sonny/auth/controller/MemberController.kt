package com.sonny.auth.controller

import com.sonny.auth.model.MemberModel
import com.sonny.auth.service.MemberService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/member")
class MemberController(
    private val memberService: MemberService
) {

    @PostMapping
    fun register(@RequestBody memberModel: MemberModel): Mono<Long> {
        return memberService.register(memberModel.loginId, memberModel.password)
    }
}
