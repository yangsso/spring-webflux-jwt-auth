package com.sonny.auth.service

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class RedisTemplateService(
    reactiveRedisTemplate: ReactiveRedisTemplate<String, Any>
) {

    private val valueOps = reactiveRedisTemplate.opsForValue()

    init {
    }

    private fun generateKey(key: String): String{
        return "auth:$key"
    }

    fun increment(key: String, value: Long): Mono<Long> {
        return valueOps.increment(generateKey(key), value)
    }

    fun set(key: String, value: Any, ttl: Duration): Mono<Boolean> {
        return valueOps.set(generateKey(key), value, ttl)
    }

    fun get(key: String): Mono<Any> {
        return valueOps.get(generateKey(key))
    }

}
