package kz.mircella.accounts.infrastructure.user

import kz.mircella.accounts.domain.user.User
import kz.mircella.accounts.domain.user.UserQueryRepository
import kz.mircella.accounts.infrastructure.elasticsearch.ElasticSearchIndexClient
import kz.mircella.accounts.infrastructure.elasticsearch.ElasticSearchQueryBuilder.createMatchQuery
import kz.mircella.accounts.infrastructure.elasticsearch.ElasticSearchQueryBuilder.findAllFieldsSatisfiedWithMatchPhraseQuery
import kz.mircella.accounts.infrastructure.elasticsearch.ElasticSearchQueryBuilder.findAllFieldsSatisfiedWithMultiMatchQuery
import kz.mircella.accounts.infrastructure.elasticsearch.ElasticSearchQueryBuilder.findByMoreLikeThisQuery
import mu.KLogging
import org.elasticsearch.index.query.QueryBuilder
import org.springframework.stereotype.Repository

@Repository
class ESUserQueryRepository(private val elasticSearchIndexClient: ElasticSearchIndexClient) : UserQueryRepository {

    override fun findAll(size: Int?): List<User> {
        return elasticSearchIndexClient.find(INDEX_NAME, User::class.java, size)
    }

    override fun findByQuery(query: String, from: Int, size: Int): List<User> {
        return findAllFieldsSatisfiedWithMatchPhraseQuery(query, FIELDS, from, size, search()).takeIf { it.isNotEmpty() }
                ?: findAllFieldsSatisfiedWithMultiMatchQuery(query, FIELDS, from, size, search()).takeIf { it.isNotEmpty() }
                ?: findByMoreLikeThisQuery(query, FIELDS, from, size, search()).takeIf { it.isNotEmpty() }
                ?: emptyList()
    }

    override fun findByLogin(login: String): User? {
        val matchQuery = createMatchQuery(login, "login")
        val users = searchUsers(matchQuery, size = 1)
        return users.find { it.login == login }
    }
    private fun search(): (QueryBuilder, Int, Int) -> List<User> = { searchQuery, from, size ->
        searchUsers(searchQuery, from, size)
    }

    private fun searchUsers(searchQuery: QueryBuilder, from: Int = 0, size: Int) =
            elasticSearchIndexClient.search(INDEX_NAME, searchQuery, User::class.java, from, size)

    companion object : KLogging() {
        private const val INDEX_NAME = "users"
        private val FIELDS = arrayOf("name", "surname", "login")
    }
}