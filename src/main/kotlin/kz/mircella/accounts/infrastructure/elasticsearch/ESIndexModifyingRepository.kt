package kz.mircella.accounts.infrastructure.elasticsearch

import kz.mircella.accounts.domain.IdentifiableObject
import kz.mircella.accounts.domain.elasticsearch.IndexModifyingRepository
import kz.mircella.accounts.infrastructure.exceptions.ESIndexNotFound
import kz.mircella.accounts.infrastructure.exceptions.ElasticSearchError
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class ESIndexModifyingRepository(private val elasticSearchIndexClient: ElasticSearchIndexClient) : IndexModifyingRepository {

    override fun <T> createIndex(indexName: String, typeName: String, clazz: Class<T>) {
        elasticSearchIndexClient.create(indexName, typeName)
        if (!indexExists(indexName)) {
            throw ESIndexNotFound(indexName)
        }
        verifyIndexIsEmpty(indexName, clazz)
        logger.info("Index $indexName was created")
    }

    override fun indexExists(indexName: String): Boolean {
        return elasticSearchIndexClient.indexExists(indexName)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> clearIndex(indexName: String, typeName: String, clazz: Class<T>) {
        val receipts = findAll(indexName, clazz) as List<IdentifiableObject>
        elasticSearchIndexClient.delete(indexName, typeName, receipts)
        logger.info("Receipts in $indexName were deleted")
    }

    override fun insert(indexName: String, typeName: String, objects: List<IdentifiableObject>) {
        val objectPackages = objects.withIndex()
                .groupBy { it.index / 300 }
                .map { entry -> entry.value.map { it.value } }
        objectPackages.forEach {
            elasticSearchIndexClient.insert(indexName, typeName, it)
        }
        logger.info("${objects.size} objects in index $indexName were inserted")
    }

    override fun insert(indexName: String, typeName: String, obj: IdentifiableObject) {
        elasticSearchIndexClient.insert(indexName, typeName, obj)
        logger.info("Object with id: '${obj.getId()}' was inserted in index $indexName")
    }

    override fun deleteIndex(indexName: String) {
        elasticSearchIndexClient.delete(indexName)
        if (elasticSearchIndexClient.indexExists(indexName)) {
            throw ElasticSearchError("Index '$indexName' was not deleted")
        }
        logger.info("Index $indexName was deleted")
    }

    private fun <T> findAll(indexName: String, clazz: Class<T>): List<T> {
        return elasticSearchIndexClient.find(indexName, clazz)
    }

    private fun <T> verifyIndexIsEmpty(indexName: String, clazz: Class<T>) {
        val entries = findAll(indexName, clazz)
        if (entries.isNotEmpty()) {
            throw ElasticSearchError("Index '$indexName' is not empty")
        }
    }

    companion object : KLogging()

}