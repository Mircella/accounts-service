package kz.mircella.accounts.infrastructure.security.authzserver

import kz.mircella.accounts.infrastructure.security.auth.UserService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.token.*
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import java.security.KeyPair
import javax.sql.DataSource

@EnableAuthorizationServer
@Configuration
@ConditionalOnProperty("security.enabled", havingValue = "true", matchIfMissing = false)
class AuthorizationServerConfig(
        private val userService: UserService,
        private val dataSource: DataSource,
        private val keyPair: KeyPair,
        private val clientDetailsService: ClientDetailsService,
        private val authenticationConfiguration: AuthenticationConfiguration
) : AuthorizationServerConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(security: AuthorizationServerSecurityConfigurer) {
//        security.allowFormAuthenticationForClients()
        security.passwordEncoder(NoOpPasswordEncoder.getInstance())
                .checkTokenAccess("permitAll()")
                .tokenKeyAccess("permitAll()")
    }

    @Throws(Exception::class)
    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.jdbc(dataSource)
    }

    @Throws(Exception::class)
    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
//        endpoints.authenticationManager(this.authenticationConfiguration.getAuthenticationManager())
//                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST).tokenStore(tokenStore())
//                .accessTokenConverter(accessTokenConverter())
        endpoints
                .tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancer())
                .authenticationManager(authenticationManager())
                .userDetailsService(userService)
                .tokenServices(defaultTokenServices())
    }

    @Bean
    fun tokenStore(): TokenStore {
        return JdbcTokenStore(dataSource)
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

//    @Bean
//    fun accessTokenConverter(): AccessTokenConverter {
//        val converter = JwtAccessTokenConverter()
//        converter.setKeyPair(this.keyPair)
//        return converter
//    }

    @Bean
    fun tokenEnhancer(): TokenEnhancer {
        val converter = JwtAccessTokenConverter()
        converter.setKeyPair(this.keyPair)
        val tokenEnhancerChain = TokenEnhancerChain()
        tokenEnhancerChain.setTokenEnhancers(listOf(converter))
        return tokenEnhancerChain
    }

    @Bean
    @Primary
    fun defaultTokenServices(): DefaultTokenServices {
        return OAuth2CustomTokenService(tokenStore(), tokenEnhancer(), clientDetailsService, authenticationManager())
    }

}
