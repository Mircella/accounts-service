package kz.mircella.accounts.infrastructure.swagger

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@Validated
@Configuration
@ConfigurationProperties(prefix = "swagger")
@ConditionalOnProperty("swagger.enabled")
class SwaggerSettings {

    @NotBlank
    lateinit var host: String

    @NotBlank
    lateinit var scheme: String

    @NotBlank
    lateinit var title: String

    @NotBlank
    lateinit var description: String
}
