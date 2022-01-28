package com.sonny.auth.config

import com.sonny.auth.properties.MariaDataSource
import io.r2dbc.pool.PoolingConnectionFactoryProvider.*
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import reactor.core.publisher.Mono
import java.time.Duration


@Configuration
@EnableR2dbcRepositories
@EnableR2dbcAuditing
class R2dbcConfig(
    private val mariaDataSource: MariaDataSource
) : AbstractR2dbcConfiguration() {

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        return ConnectionFactories.get(builder()
            .option(DRIVER, "pool")
            .option(PROTOCOL, "mariadb")
            .option(HOST, mariaDataSource.host)
            .option(PORT, mariaDataSource.port)
            .option(USER, mariaDataSource.username)
            .option(PASSWORD, mariaDataSource.password)
            .option(DATABASE, mariaDataSource.database)
            .option(MAX_SIZE, 2)
            .option(INITIAL_SIZE, 2)
            .option(MAX_IDLE_TIME, Duration.ofSeconds(10))
            .option(MAX_CREATE_CONNECTION_TIME, Duration.ofSeconds(5))
            .option(MAX_LIFE_TIME, Duration.ofMinutes(10))
            .build()
        )
    }

    @Bean
    fun myAuditorProvider(): ReactiveAuditorAware<String> {
        return ReactiveAuditorAware { Mono.just("test") }
    }

}
