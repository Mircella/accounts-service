package kz.mircella.accounts.infrastructure.accounts

import kz.mircella.accounts.domain.accounts.Account
import kz.mircella.accounts.domain.accounts.AccountData
import kz.mircella.accounts.domain.accounts.AccountsRepository
import kz.mircella.accounts.domain.accounts.AccountsService
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.UUID

@Service
class DefaultAccountsService(private val accountsRepository: AccountsRepository) : AccountsService {

    override fun findByLogin(login: String): Account {
        return accountsRepository.findByLogin(login) ?: throw AccountNotFound(login)
    }

    override fun save(data: AccountData) {
        accountsRepository.save(Account(UUID.randomUUID(), data.login, data.password))
    }
}

class AccountNotFound(login: String) : RuntimeException("Account with login:'$login' not found")

class AccountWithSuchLoginAlreadyExists(login: String) : RuntimeException("Account with login:'$login' already exists")
