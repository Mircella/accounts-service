package kz.mircella.accounts.domain.accounts

interface AccountsService {
    fun findByLogin(login: String): Account

    fun save(data: AccountData)
}