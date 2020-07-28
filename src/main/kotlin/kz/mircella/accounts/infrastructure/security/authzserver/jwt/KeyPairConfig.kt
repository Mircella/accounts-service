package kz.mircella.accounts.infrastructure.security.authzserver.jwt

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.KeyPair

@Configuration
class KeyPairConfig {

    @Bean
    fun keyPair(jwtJwksKeyService: JwtJwksKeyService): KeyPair {
        return jwtJwksKeyService.generateKeyPair()
    }
}