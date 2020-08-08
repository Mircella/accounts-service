package kz.mircella.accounts.infrastructure.security

import kz.mircella.accounts.infrastructure.security.auth.UserService
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnProperty("security.enabled", havingValue = "true")
class ApplicationSecurityConfiguration(
        private val userService: UserService,
        private val authenticationProvider: AuthenticationProvider
) : WebSecurityConfigurerAdapter() {

//    @Bean
//    fun authoritiesMapper(): GrantedAuthoritiesMapper {
//        val authorityMapper = SimpleAuthorityMapper()
//        authorityMapper.setConvertToUpperCase(true)
//        authorityMapper.setDefaultAuthority("USER")
//        return authorityMapper
//    }

//    @Bean
//    fun authenticationProvider(): DaoAuthenticationProvider {
//        val provider = DaoAuthenticationProvider()
//        provider.setUserDetailsService(userService)
//        provider.setPasswordEncoder(BCryptPasswordEncoder(11))
//        provider.setAuthoritiesMapper(authoritiesMapper())
//        return provider
//    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return NoOpPasswordEncoder.getInstance()
    }

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(authenticationProvider)
    }

    override fun configure(web: WebSecurity?) {
        super.configure(web)
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }


    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .requestMatchers(EndpointRequest.to("health")).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR")
                .and()
//                .anonymous().disable()
                .csrf().disable()
//        http
//                .csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/", "/index", "/css/*", "/js/*").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                .loginPage("/login").permitAll()
//                .and()
//                .logout().invalidateHttpSession(true)
//                .clearAuthentication(true)
//                .logoutRequestMatcher(AntPathRequestMatcher("/logout"))
//                .logoutSuccessUrl("/logout-success").permitAll()
    }
}