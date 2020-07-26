package kz.mircella.accounts.infrastructure.receipts

import kz.mircella.accounts.domain.receipts.Receipt
import kz.mircella.accounts.domain.receipts.ReceiptQueryRepository
import kz.mircella.accounts.infrastructure.elasticsearch.ElasticSearchIndexClient
import kz.mircella.accounts.infrastructure.elasticsearch.ElasticSearchQueryBuilder.createMatchQuery
import kz.mircella.accounts.infrastructure.elasticsearch.ElasticSearchQueryBuilder.findAllFieldsSatisfiedWithMatchPhraseQuery
import kz.mircella.accounts.infrastructure.elasticsearch.ElasticSearchQueryBuilder.findAllFieldsSatisfiedWithMultiMatchQuery
import kz.mircella.accounts.infrastructure.elasticsearch.ElasticSearchQueryBuilder.findByMoreLikeThisQuery
import mu.KLogging
import org.elasticsearch.index.query.QueryBuilder
import org.springframework.stereotype.Repository

@Repository
class ESReceiptQueryRepository(private val elasticSearchIndexClient: ElasticSearchIndexClient) : ReceiptQueryRepository {

    override fun findAll(size: Int?): List<Receipt> {
        return elasticSearchIndexClient.find(INDEX_NAME, Receipt::class.java, size)
    }

    override fun findByQuery(query: String, from: Int, size: Int): List<Receipt> {
        return findAllFieldsSatisfiedWithMatchPhraseQuery(query, FIELDS, from, size, search()).takeIf { it.isNotEmpty() }
                ?: findAllFieldsSatisfiedWithMultiMatchQuery(query, FIELDS, from, size, search()).takeIf { it.isNotEmpty() }
                ?: findByMoreLikeThisQuery(query, FIELDS, from, size, search()).takeIf { it.isNotEmpty() }
                ?: emptyList()
    }

    override fun findByTitle(title: String): Receipt? {
        val matchQuery = createMatchQuery(title, "title")
        val receipts = searchReceipts(matchQuery, size = 1)
        return receipts.find { it.title == title }
    }

    override fun findByAuthor(authorName: String): Receipt? {
        val matchQuery = createMatchQuery(authorName, "author.name")
        val receipts = searchReceipts(matchQuery, size = 1)
        return receipts.find { it.author.name == authorName }
    }

    private fun search(): (QueryBuilder, Int, Int) -> List<Receipt> = { searchQuery, from, size ->
        searchReceipts(searchQuery, from, size)
    }

    private fun searchReceipts(searchQuery: QueryBuilder, from: Int = 0, size: Int) =
            elasticSearchIndexClient.search(INDEX_NAME, searchQuery, Receipt::class.java, from, size)

    companion object : KLogging() {
        private const val INDEX_NAME = "receipts"
        private val FIELDS = arrayOf("title", "description", "createdAt", "author.name", "author.surname")
    }

}