package kz.mircella.accounts

import java.util.UUID

data class UserLoggedInEvent(val id: UUID, val login: String)