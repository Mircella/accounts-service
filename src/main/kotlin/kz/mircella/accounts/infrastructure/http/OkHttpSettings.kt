package kz.mircella.accounts.infrastructure.http

import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "http-client")
data class OkHttpSettings(
        val logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC,
        val followRedirects: Boolean = true
)
