package kz.mircella.accounts.infrastructure.security.resourceserver

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.DefaultTokenServices

@ConditionalOnProperty("security.enabled", havingValue = "true", matchIfMissing = false)
@Configuration
@EnableResourceServer
class ResourceServerConfiguration(
        private val tokenServices: DefaultTokenServices
) : ResourceServerConfigurerAdapter() {

    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.tokenServices(tokenServices)
    }

    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .requestMatchers(EndpointRequest.to("health")).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR")
                .and()
                .anonymous().disable()
                .authorizeRequests().antMatchers("/**").authenticated()
                .and()
                .csrf().disable()
    }
}