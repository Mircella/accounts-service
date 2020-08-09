package kz.mircella.accounts.domain.accounts

import java.util.UUID

data class Account(val id: UUID, val login: String, val password: String)