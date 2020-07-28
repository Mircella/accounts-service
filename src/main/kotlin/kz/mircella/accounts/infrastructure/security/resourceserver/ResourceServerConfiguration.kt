package kz.mircella.accounts.infrastructure.security.resourceserver

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.DefaultTokenServices

@Configuration
@EnableResourceServer
class ResourceServerConfiguration(
        private val tokenServices: DefaultTokenServices
) : ResourceServerConfigurerAdapter() {

    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.tokenServices(tokenServices)
    }

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests().antMatchers("/**").authenticated()
    }
}