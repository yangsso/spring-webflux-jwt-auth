package com.sonny.auth.repository

import com.sonny.auth.entity.MemberEntity
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface MemberRepository : R2dbcRepository<MemberEntity, Long> {
    fun findByLoginId(loginId: String) : Mono<MemberEntity>
    fun findByLoginIdAndPassword(loginId: String, password: String) : Mono<MemberEntity>
}
