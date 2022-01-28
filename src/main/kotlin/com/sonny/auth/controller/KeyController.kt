package com.sonny.auth.controller

import com.sonny.auth.entity.KeyEntity
import com.sonny.auth.service.KeyService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/key")
class KeyController(
    val keyService: KeyService
) {

    @PostMapping("/{serviceName}")
    fun generateKey(@PathVariable serviceName: String): Mono<String> {
        return keyService.generateKey(serviceName)
    }

}
