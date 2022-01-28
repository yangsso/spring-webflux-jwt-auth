package com.sonny.auth.config

import org.jasypt.encryption.StringEncryptor
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class CyptoConfig {
    @Value("\${jasypt.encryptor.password}")
    var password: String? = null

    @Bean("jasyptStringEncryptor")
    fun stringEncryptor(): StringEncryptor {
        val encryptor = StandardPBEStringEncryptor()
        encryptor.setPassword(password)
        encryptor.setAlgorithm("PBEWithMD5AndDES")

        return encryptor
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(4)
    }

}
