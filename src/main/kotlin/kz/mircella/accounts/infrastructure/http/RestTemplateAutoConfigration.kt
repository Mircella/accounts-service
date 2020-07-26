package kz.mircella.accounts.infrastructure.http

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory
import org.springframework.validation.annotation.Validated
import org.springframework.web.client.RestTemplate
import java.io.IOException
import java.util.ArrayList

@Validated
@Configuration
@EnableConfigurationProperties(OkHttpSettings::class)
class RestTemplateAutoConfiguration(private val okHttpSettings: OkHttpSettings) {

    @Bean
    @Primary
    fun createRestTemplate(builder: RestTemplateBuilder, mapper: ObjectMapper): RestTemplate {
        return builder
                .errorHandler(RestTemplateResponseErrorHandler(HttpErrorResponseExceptionFactory(mapper)))
                .additionalCustomizers(RestTemplateCustomizer { restTemplate: RestTemplate -> restTemplate.requestFactory = OkHttp3ClientHttpRequestFactory(createOkHttpClient()) }
                )
                .build()
    }

    @Bean
    fun createOkHttpClient(): OkHttpClient {
        logger.info("Setting OkHttp log level: {}", okHttpSettings.logLevel)
        val logging: Interceptor = AuthCommonsHttpLoggingInterceptor(
                HttpLoggingInterceptor.Logger { message: String? -> logger.info("http: {}", message) },
                okHttpSettings.logLevel
        )
        return OkHttpClient().newBuilder()
                .followRedirects(okHttpSettings.followRedirects)
                .followSslRedirects(okHttpSettings.followRedirects)
                .addInterceptor(logging)
                .build()
    }

    internal class AuthCommonsHttpLoggingInterceptor(private val logger: HttpLoggingInterceptor.Logger, val level: HttpLoggingInterceptor.Level) : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val container = LogContainer()
            val delegate = HttpLoggingInterceptor(container)
            delegate.level = level
            return try {
                delegate.intercept(chain)
            } finally {
                logger.log(container.concat())
            }
        }

    }

    internal class LogContainer : HttpLoggingInterceptor.Logger {
        private val messages: MutableList<String> = ArrayList()
        override fun log(message: String) {
            messages.add(message)
        }

        fun concat(): String {
            return java.lang.String.join("\u26a0", messages)
        }
    }

    private companion object: KLogging()
}
