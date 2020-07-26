package kz.mircella.accounts

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class AccountsController(private val accountsRepository: AccountsRepository) {

    @GetMapping("/accounts")
    fun getAccounts(): List<Account> {
        return accountsRepository.getAll()
    }

    @PostMapping("/accounts")
    fun saveAccount() {
        val randomAccount = randomAccount()
        accountsRepository.save(randomAccount)
    }

    private fun randomAccount(): Account {
        val account = Account(UUID.randomUUID(), "${RandomStringUtils.randomAlphanumeric(10)}@mail.com")
        return account
    }
}