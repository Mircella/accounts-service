package kz.mircella.accounts.infrastructure.web.api

import kz.mircella.accounts.domain.accounts.Account
import kz.mircella.accounts.domain.accounts.AccountData
import kz.mircella.accounts.domain.accounts.AccountsRepository
import kz.mircella.accounts.domain.accounts.AccountsService
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
class AccountsController(
        private val accountsService: AccountsService
) {

    @GetMapping("/accounts/{login}")
    fun getAccountById(@PathVariable login: String): Account {
        return accountsService.findByLogin(login)
    }

    @PostMapping("/accounts")
    fun saveAccount(@RequestBody request: AccountCreationRequest) {
        accountsService.save(request.toAccountData())
    }
}


data class AccountCreationRequest(val login: String, val password: String){
    fun toAccountData() = AccountData(login, password)
}