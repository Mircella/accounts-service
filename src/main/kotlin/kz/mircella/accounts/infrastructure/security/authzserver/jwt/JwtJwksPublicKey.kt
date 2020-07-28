package kz.mircella.accounts.infrastructure.security.authzserver.jwt

import java.time.LocalDateTime

data class JwtJwksPublicKey(
        val createdDate: LocalDateTime,
        val encodedPublicKey: String,
        val publicKey: String
)