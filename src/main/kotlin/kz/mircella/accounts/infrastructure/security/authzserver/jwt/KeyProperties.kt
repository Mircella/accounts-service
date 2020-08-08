package kz.mircella.accounts.infrastructure.security.authzserver.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.util.StringUtils
import javax.annotation.PostConstruct

@ConstructorBinding
@ConfigurationProperties(prefix = "jwt.jwks")
data class KeyProperties(
        val publicKey: String,
        val privateKey: String,
        val keyStoreCapacity:Int = DEFAULT_KEYSTORE_SIZE
) {
    @PostConstruct
    fun validate() {
        if (StringUtils.isEmpty(publicKey) || StringUtils.isEmpty(privateKey)) {
            throw KeyMissingException()
        }
        if (keyStoreCapacity < 1) {
            throw InvalidKeyStoreCapacityException()
        }
    }

    companion object {
        private const val DEFAULT_KEYSTORE_SIZE = 10
    }
}

class KeyMissingException : RuntimeException("Private and public keys have to be set!")

class InvalidKeyStoreCapacityException : RuntimeException("KeyStore capacity must be > 0")
