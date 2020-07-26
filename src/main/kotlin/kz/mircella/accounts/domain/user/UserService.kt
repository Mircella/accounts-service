package kz.mircella.accounts.domain.user

import kz.mircella.accounts.domain.IndexOperationResult
import java.util.UUID

interface UserService {
    fun saveUsers(users: List<CreateUserCommand>): IndexOperationResult
    fun getAllUsers(size: Int?): List<User>
    fun findByQuery(query: String, size: Int? = 3): List<User>
    fun findByLogin(login: String): User
    fun createUsersIndex(): IndexOperationResult
    fun deleteUsersIndex(): IndexOperationResult
    fun deleteAllUsers(): IndexOperationResult
}

data class CreateUserCommand(val name: String, val surname: String, val login: String) {
    fun toUser() = User(UUID.randomUUID(), login, name, surname)
}