package kz.mircella.accounts.infrastructure.security.authzserver

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.TokenRequest
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenEnhancer
import org.springframework.security.oauth2.provider.token.TokenStore

open class OAuth2CustomTokenService(
        private val tokenStore: TokenStore,
        private val tokenEnhancer: TokenEnhancer,
        private val clientDetailsService: ClientDetailsService,
        private val authenticationManager: AuthenticationManager
) : DefaultTokenServices() {

    init {
        setTokenStore(tokenStore)
        setSupportRefreshToken(true)
        setTokenEnhancer(tokenEnhancer)
        setClientDetailsService(clientDetailsService)
        setAuthenticationManager(authenticationManager)
    }

    override fun refreshAccessToken(refreshTokenValue: String?, tokenRequest: TokenRequest): OAuth2AccessToken {
        return super.refreshAccessToken(refreshTokenValue, tokenRequest)
    }

    override fun createAccessToken(authentication: OAuth2Authentication?): OAuth2AccessToken {
        return super.createAccessToken(authentication)
    }
}
