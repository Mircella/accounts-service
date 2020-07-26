package kz.mircella.accounts.infrastructure.products

import kz.mircella.accounts.domain.products.Product
import kz.mircella.accounts.domain.products.ProductQueryRepository
import kz.mircella.accounts.infrastructure.elasticsearch.ElasticSearchIndexClient
import kz.mircella.accounts.infrastructure.elasticsearch.ElasticSearchQueryBuilder
import kz.mircella.accounts.infrastructure.elasticsearch.ElasticSearchQueryBuilder.findAllFieldsSatisfiedWithMatchPhraseQuery
import kz.mircella.accounts.infrastructure.elasticsearch.ElasticSearchQueryBuilder.findAllFieldsSatisfiedWithMultiMatchQuery
import kz.mircella.accounts.infrastructure.elasticsearch.ElasticSearchQueryBuilder.findByMoreLikeThisQuery
import mu.KLogging
import org.elasticsearch.index.query.QueryBuilder
import org.springframework.stereotype.Repository

@Repository
class ESProductQueryRepository(private val elasticSearchIndexClient: ElasticSearchIndexClient) : ProductQueryRepository {

    @Suppress("UNCHECKED_CAST")
    override fun findAll(size: Int?): List<Product> {
        return elasticSearchIndexClient.find(INDEX_NAME, Product::class.java, size)
    }

    override fun findByName(name: String): Product? {
        val matchQuery = ElasticSearchQueryBuilder.createMatchQuery(name, "name")
        val receipts = searchProducts(matchQuery, size = 1)
        return receipts.find { it.name == name }
    }

    override fun findByQuery(query: String, from: Int, size: Int): List<Product> {
        return findAllFieldsSatisfiedWithMatchPhraseQuery(query, FIELDS, from, size, search()).takeIf { it.isNotEmpty() }
                ?: findAllFieldsSatisfiedWithMultiMatchQuery(query, FIELDS, from, size, search()).takeIf { it.isNotEmpty() }
                ?: findByMoreLikeThisQuery(query, FIELDS, from, size, search()).takeIf { it.isNotEmpty() }
                ?: emptyList()
    }

    private fun search(): (QueryBuilder, Int, Int) -> List<Product> = { searchQuery, from, size ->
        searchProducts(searchQuery, from, size)
    }

    private fun searchProducts(searchQuery: QueryBuilder, from: Int = 0, size: Int) =
            elasticSearchIndexClient.search(INDEX_NAME, searchQuery, Product::class.java, from, size)

    companion object : KLogging() {
        private const val INDEX_NAME = "products"
        private val FIELDS = arrayOf("name", "description")
    }

}