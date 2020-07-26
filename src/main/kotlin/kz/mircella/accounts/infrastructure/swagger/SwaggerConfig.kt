package kz.mircella.accounts.infrastructure.swagger

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.lang.NonNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Validated
@Configuration
@EnableSwagger2
@ConditionalOnProperty("swagger.enabled")
class SwaggerConfig(private val swaggerSettings: SwaggerSettings,
                    @param:Value("\${info.build.version}") @param:NonNull private val appVersion: String) {

    @Bean
    fun springFoxConfig(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .protocols(hashSetOf(swaggerSettings.scheme))
                .forCodeGeneration(true)
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, arrayListOf())
                .apiInfo(metadata())

    }

    private fun metadata(): ApiInfo {
        return ApiInfoBuilder()
                .title(swaggerSettings.title)
                .description(swaggerSettings.description)
                .version(appVersion)
                .build()
    }
}