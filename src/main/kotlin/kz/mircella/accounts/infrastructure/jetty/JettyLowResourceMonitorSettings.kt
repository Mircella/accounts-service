package kz.mircella.accounts.infrastructure.jetty

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "jetty.low-resources")
data class JettyLowResourceMonitorSettings(
        // all durations in milliseconds
        val period: Int = 1000,
        val idleTimeout: Int = 200,
        val monitorThreads: Boolean = true,
        val maxConnections: Int = 0, // do not monitor connections
        val maxMemory: Long = 0, // do not monitor memory
        val maxLowResourcesTime: Int = 5000
)