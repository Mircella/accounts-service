package kz.mircella.accounts.domain.user

import kz.mircella.accounts.domain.IdentifiableObject
import java.util.UUID

data class User(
        val id: UUID,
        val login: String,
        val name: String,
        val surname: String
) : IdentifiableObject {
    override fun getId(): String = id.toString()

    fun toDetails() = UserDetails(login)
}

data class UserDetails(val login: String)
