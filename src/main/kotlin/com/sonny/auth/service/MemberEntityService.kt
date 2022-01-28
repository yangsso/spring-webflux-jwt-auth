package com.sonny.auth.service

import com.sonny.auth.entity.MemberEntity
import com.sonny.auth.repository.MemberRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MemberEntityService(
    private val memberRepository: MemberRepository
){
    fun add(memberEntity: MemberEntity): Mono<MemberEntity> {
        return memberRepository.save(memberEntity)
    }

    fun findByLoginId(loginId: String): Mono<MemberEntity> {
        return memberRepository.findByLoginId(loginId)
    }

    fun findById(memberId: Long): Mono<MemberEntity> {
        return memberRepository.findById(memberId)
    }
}
