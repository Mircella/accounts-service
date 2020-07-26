package kz.mircella.accounts.infrastructure.jetty

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "jetty.thread-pool")
data class JettyThreadPoolSettings(
        val minThreads: Int = 8,
        val maxThreads: Int = 128,
        val idleTimeout: Int = 30000, // milliseconds
        val detailedDump: Boolean = false
)