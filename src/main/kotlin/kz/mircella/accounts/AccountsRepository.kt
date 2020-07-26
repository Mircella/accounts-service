package kz.mircella.accounts

import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.UUID

@Repository
class AccountsRepository(private val jdbc: NamedParameterJdbcTemplate) {

    fun save(account: Account) {
        jdbc.update(
                """INSERT INTO accounts (id, login) 
                VALUES (:id, :login) 
                ON CONFLICT(id) DO UPDATE 
                SET login = EXCLUDED.login"""
                        .trimMargin(),
                mapOf("id" to account.id, "login" to account.login)
        )
    }

    fun getAll(): List<Account> {
        val accounts = jdbc.query("SELECT * FROM accounts", object: RowMapper<Account> {
            override fun mapRow(resultSet: ResultSet, rowNum: Int): Account? {
                val id = UUID.fromString(resultSet.getString("id"))
                val login = resultSet.getString("login")
                return Account(id, login)
            }
        })
        return accounts
    }

}