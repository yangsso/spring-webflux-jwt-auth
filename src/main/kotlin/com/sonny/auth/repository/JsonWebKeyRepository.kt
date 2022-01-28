package com.sonny.auth.repository

import com.sonny.auth.entity.KeyEntity
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface JsonWebKeyRepository : R2dbcRepository<KeyEntity, Long> {
    fun findByKid(kid: String) : Mono<KeyEntity>
    fun findByService(service: String): Mono<KeyEntity>
}
