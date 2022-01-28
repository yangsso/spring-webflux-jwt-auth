package com.sonny.auth.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "datasource")
data class MariaDataSource (
    val username: String,
    val password: String,
    val host: String,
    val port: Int,
    val database: String
){

    companion object {

    }
}
