package kz.mircella.accounts.infrastructure.security.auth

class AuthUserRepository {

    fun findByUsername(userName: String): AuthUser? {
        return AUTH_USERS[userName]
    }

    private companion object {
        val AUTH_USERS = mapOf(
                "user1" to AuthUser(username = "user1", password = "\$2y\$12\$j8dRMu/IVFsmNRJr7vGkIuZdUcDCugOw4CKyyBMZRMOC25XRfNlOu"),
                "user2" to AuthUser(username = "user2", password = "\$2y\$12\$j8dRMu/IVFsmNRJr7vGkIuZdUcDCugOw4CKyyBMZRMOC25XRfNlOu"),
                "user3" to AuthUser(username = "user3", password = "\$2y\$12\$j8dRMu/IVFsmNRJr7vGkIuZdUcDCugOw4CKyyBMZRMOC25XRfNlOu"),
                "user4" to AuthUser(username = "user4", password = "\$2y\$12\$j8dRMu/IVFsmNRJr7vGkIuZdUcDCugOw4CKyyBMZRMOC25XRfNlOu"),
                "user5" to AuthUser(username = "user5", password = "\$2y\$12\$j8dRMu/IVFsmNRJr7vGkIuZdUcDCugOw4CKyyBMZRMOC25XRfNlOu")
        )
    }
}