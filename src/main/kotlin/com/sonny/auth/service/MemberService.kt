package com.sonny.auth.service

import com.sonny.auth.entity.MemberEntity
import com.sonny.auth.enum.Role
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MemberService(
    private val memberEntityService: MemberEntityService,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(loginId: String, password: String): Mono<Long> {
        return memberEntityService.add(MemberEntity(loginId, passwordEncoder.encode(password), Role.ROLE_ADMIN.name))
            .map(MemberEntity::id)
    }

    fun findMemberByLoginModel(loginId: String, password: String): Mono<MemberEntity> {
        return memberEntityService.findByLoginId(loginId)
            .switchIfEmpty(Mono.empty())
            .filter{ passwordEncoder.matches(password, it.password) }
    }

    fun findMemberByLoginId(loginId: String): Mono<MemberEntity> {
        return memberEntityService.findByLoginId(loginId)
    }

    fun getMember(memberId: Long) : Mono<MemberEntity> {
        return memberEntityService.findById(memberId)
    }
}
