package kz.mircella.accounts.domain.user

interface UserQueryRepository {

    fun findAll(size: Int?): List<User>
    fun findByQuery(query: String, from: Int, size: Int): List<User>
    fun findByLogin(login: String): User?
}