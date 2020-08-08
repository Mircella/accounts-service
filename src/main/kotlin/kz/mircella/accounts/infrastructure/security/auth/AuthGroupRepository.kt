package kz.mircella.accounts.infrastructure.security.auth

class AuthGroupRepository {
    fun findByUsername(username: String): List<AuthGroup> {
        return USERS_AUTH_GROUPS.filter { it.username == username }
    }

    private companion object {
        val USERS_AUTH_GROUPS = listOf(
                AuthGroup(username = "user1", authGroup = "USER"),
                AuthGroup(username = "user1", authGroup = "ADMIN"),
                AuthGroup(username = "user2", authGroup = "USER"),
                AuthGroup(username = "user3", authGroup = "USER"),
                AuthGroup(username = "user4", authGroup = "USER"),
                AuthGroup(username = "user5", authGroup = "ADMIN")
        )
    }
}