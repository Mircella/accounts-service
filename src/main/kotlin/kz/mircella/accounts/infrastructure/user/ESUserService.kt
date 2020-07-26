package kz.mircella.accounts.infrastructure.user

import kz.mircella.accounts.domain.IndexOperationResult
import kz.mircella.accounts.domain.elasticsearch.IndexModifyingRepository
import kz.mircella.accounts.domain.user.CreateUserCommand
import kz.mircella.accounts.domain.user.User
import kz.mircella.accounts.domain.user.UserQueryRepository
import kz.mircella.accounts.domain.user.UserService
import kz.mircella.accounts.infrastructure.exceptions.ESIndexAlreadyExists
import kz.mircella.accounts.infrastructure.exceptions.UserNotFound
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class ESUserService(
        private val userQueryRepository: UserQueryRepository,
        private val indexModifyingRepository: IndexModifyingRepository
) : UserService {

    override fun findByQuery(query: String, size: Int?): List<User> {
        return userQueryRepository.findByQuery(query, 0, size ?: DEFAULT_SIZE)
    }

    override fun findByLogin(login: String): User {
        return userQueryRepository.findByLogin(login) ?: throw UserNotFound(login)
    }

    override fun saveUsers(users: List<CreateUserCommand>): IndexOperationResult {
        val message = if (indexModifyingRepository.indexExists(INDEX_NAME)) {
            indexModifyingRepository.insert(INDEX_NAME, TYPE_NAME, users.map { it.toUser() })
            "Users into $INDEX_NAME were inserted successfully"
        } else {
            "Index $INDEX_NAME does not exist"
        }
        return IndexOperationResult((message))
    }

    override fun getAllUsers(size: Int?): List<User> {
        return userQueryRepository.findAll(size ?: DEFAULT_SIZE)
    }

    override fun createUsersIndex(): IndexOperationResult {
        if (indexModifyingRepository.indexExists(INDEX_NAME)) {
            throw ESIndexAlreadyExists(INDEX_NAME)
        }
        indexModifyingRepository.createIndex(INDEX_NAME, TYPE_NAME, User::class.java)
        return IndexOperationResult("Index $INDEX_NAME was created successfully")
    }

    override fun deleteUsersIndex(): IndexOperationResult {
        indexModifyingRepository.deleteIndex(INDEX_NAME)
        return IndexOperationResult("Index $INDEX_NAME was deleted successfully")
    }

    override fun deleteAllUsers(): IndexOperationResult {
        indexModifyingRepository.clearIndex(INDEX_NAME, TYPE_NAME, User::class.java)
        return IndexOperationResult("Index $INDEX_NAME was cleared successfully")
    }

    companion object : KLogging() {
        private const val DEFAULT_SIZE = 10
        private const val INDEX_NAME = "users"
        private const val TYPE_NAME = "user"
    }
}


