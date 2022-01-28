package com.sonny.auth.service

import com.sonny.auth.entity.KeyEntity
import com.sonny.auth.repository.JsonWebKeyRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class KeyEntityService(
    private val jsonWebKeyRepository: JsonWebKeyRepository
){
    fun add(keyEntity: KeyEntity): Mono<KeyEntity> {
        return jsonWebKeyRepository.save(keyEntity)
    }

    fun findByKid(kid: String): Mono<KeyEntity> {
        return jsonWebKeyRepository.findByKid(kid)
    }

    fun findByService(service: String): Mono<KeyEntity> {
        return jsonWebKeyRepository.findByService(service)
    }
}
