package kz.mircella.accounts.domain.accounts

import kz.mircella.accounts.infrastructure.accounts.AccountWithSuchLoginAlreadyExists
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.UUID

@Repository
class AccountsRepository(private val jdbc: NamedParameterJdbcTemplate) {

    fun save(account: Account) {
        try {
            jdbc.update(
                    """
                    INSERT INTO accounts (id, login, password) 
                    VALUES (:id, :login, :password) 
                    ON CONFLICT(id) DO UPDATE 
                    SET login = EXCLUDED.login, 
                    password = EXCLUDED.password
                    """.trimMargin(),
                    mapOf(
                            "id" to account.id,
                            "login" to account.login,
                            "password" to account.password
                    )
            )
        } catch (e: DuplicateKeyException) {
            throw AccountWithSuchLoginAlreadyExists(account.login)
        }
    }

    fun findByLogin(login: String): Account? {
        return try {
            jdbc.queryForObject("SELECT * FROM accounts WHERE login = :login", mapOf("login" to login), AccountRowMapper)
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    fun getAll(): List<Account> {
        return jdbc.query("SELECT * FROM accounts", AccountRowMapper)
    }

    private companion object AccountRowMapper : RowMapper<Account> {
        override fun mapRow(resultSet: ResultSet, rowNum: Int): Account? {
            val id = UUID.fromString(resultSet.getString("id"))
            val login = resultSet.getString("login")
            val password = resultSet.getString("password")
            return Account(id, login, password)
        }
    }
}