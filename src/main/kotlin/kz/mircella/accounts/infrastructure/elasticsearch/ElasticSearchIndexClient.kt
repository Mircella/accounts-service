package kz.mircella.accounts.infrastructure.elasticsearch

import kz.mircella.accounts.domain.IdentifiableObject
import kz.mircella.accounts.infrastructure.exceptions.ElasticSearchError
import mu.KLogging
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.admin.indices.get.GetIndexRequest
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.update.UpdateRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ElasticSearchIndexClient(
        private val restHighLevelClient: RestHighLevelClient,
        private val elasticSearchResultExtractor: ElasticSearchResultExtractor
) {

    fun create(indexName: String, typeName: String) {
        val request = CreateIndexRequest(indexName)
        request.settings(Settings.builder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 1))
        try {
            restHighLevelClient.indices().create(request, RequestOptions.DEFAULT)
        } catch (e: Exception) {
            logger.error("Failed to create index: '$indexName', reason: '${e.localizedMessage}'")
            throw ElasticSearchError("Failed to create index '$indexName'")
        }
    }

    fun delete(indexName: String) {
        val request = DeleteIndexRequest(indexName)
        try {
            restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT)
        } catch (e: Exception) {
            logger.error("Failed to delete index: '$indexName', reason: '${e.localizedMessage}'")
            throw ElasticSearchError("Failed to delete index: '$indexName'")
        }
    }

    fun indexExists(indexName: String): Boolean {
        val request = GetIndexRequest().indices(indexName)
        try {
            return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT)
        } catch (e: Exception) {
            logger.error("Failed to check index '$indexName' exists, reason: '${e.localizedMessage}'")
            throw ElasticSearchError("Failed to check index '$indexName' exists")
        }

    }

    fun <T> find(indexName: String, clazz: Class<T>, size: Int? = null): List<T> {
        val searchRequest = prepareSearchRequest(indexName, QueryBuilders.matchAllQuery(), size = size)
        val searchResponse = invokeSearchRequest(searchRequest)
        return elasticSearchResultExtractor.parseSearchResponse(searchResponse, clazz)
    }

    fun insert(indexName: String, typeName: String, objectsToInsert: List<IdentifiableObject>) {
        try {
            val bulkRequest = BulkRequest()
            val indexRequest = objectsToInsert.map {
                val serializedObject = elasticSearchResultExtractor.objectAsJson(it)
                IndexRequest(indexName, typeName, it.getId()).source(serializedObject, XContentType.JSON)
            }
            indexRequest.forEach { bulkRequest.add(it) }
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT)
        } catch (e: Exception) {
            logger.warn("Failed to insert items: '${objectsToInsert.joinToString(",", prefix = "[", postfix = "]") { it.getId() }}', reason: ${e.localizedMessage}")
            throw ElasticSearchError(
                    """Exception while inserting items: 
                    '${objectsToInsert.joinToString(",", prefix = "[", postfix = "]") { it.getId() }}' 
                    to ES instance""".trimMargin()
            )
        }
    }

    fun insert(indexName: String, typeName: String, objectToInsert: IdentifiableObject) {
        try {
            val serializedObject = elasticSearchResultExtractor.objectAsJson(objectToInsert)
            val indexRequest = IndexRequest(indexName, typeName, objectToInsert.getId())
                    .source(serializedObject, XContentType.JSON)
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT)
        } catch (e: Exception) {
            logger.warn("Failed to insert item: '${objectToInsert.getId()}}', reason: ${e.localizedMessage}")
            throw ElasticSearchError(
                    """Exception while inserting item: 
                    '${objectToInsert.getId()}}' 
                    to ES instance""".trimMargin()
            )
        }
    }

    fun update(indexName: String, typeName: String, objectsToUpdate: List<IdentifiableObject>) {
        try {
            val bulkRequest = BulkRequest()
            val updateRequests = objectsToUpdate.map {
                val serializedObject = elasticSearchResultExtractor.objectAsJson(it)
                UpdateRequest(indexName, typeName, it.getId()).doc(serializedObject, XContentType.JSON)
            }
            updateRequests.forEach { bulkRequest.add(it) }
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT)
        } catch (e: Exception) {
            logger.warn("Failed to insert items: '${objectsToUpdate.joinToString(",", prefix = "[", postfix = "]") { it.getId() }}', reason: ${e.localizedMessage}")
            throw ElasticSearchError(
                    """Exception while updating items: 
                    '${objectsToUpdate.joinToString(",", prefix = "[", postfix = "]") { it.getId() }}' 
                    to ES instance""".trimMargin()
            )
        }
    }

    fun delete(indexName: String, typeName: String, objectsToDelete: List<IdentifiableObject>) {
        try {
            val bulkRequest = BulkRequest()
            val deleteRequests = objectsToDelete.map {
                DeleteRequest(indexName, typeName, it.getId())
            }
            deleteRequests.forEach { bulkRequest.add(it) }
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT)
        } catch (e: Exception) {
            logger.warn("Failed to delete items: '${objectsToDelete.joinToString(",", prefix = "[", postfix = "]") { it.getId() }}', reason: ${e.localizedMessage}")
            throw ElasticSearchError(
                    """Exception while deleting items: 
                    '${objectsToDelete.joinToString(",", prefix = "[", postfix = "]") { it.getId() }}' 
                    to ES instance""".trimMargin()
            )
        }
    }

    fun <T> search(indexName: String, searchQuery: QueryBuilder, clazz: Class<T>, from: Int = 0, size: Int): List<T> {
        val searchRequest = prepareSearchRequest(indexName, searchQuery, from, size)
        val searchResponse = invokeSearchRequest(searchRequest)
        return elasticSearchResultExtractor.parseSearchResponse(searchResponse, clazz)
    }

    private fun prepareSearchRequest(indexName: String, query: QueryBuilder, from: Int = 0, size: Int? = null): SearchRequest {
        val searchRequest = SearchRequest().indices(indexName)
        val searchSourceBuilder = SearchSourceBuilder()
        searchSourceBuilder.query(query)
                .from(from)
                .timeout(TimeValue(10, TimeUnit.SECONDS))
        size?.let { searchSourceBuilder.size(it) }
        searchRequest.source(searchSourceBuilder)
        return searchRequest
    }

    private fun invokeSearchRequest(searchRequest: SearchRequest): SearchResponse {
        try {
            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT)
        } catch (e: Exception) {
            logger.warn("Failed to invoke search request item: '${searchRequest.description}', reason: ${e.localizedMessage}")
            throw ElasticSearchError("Exception while invoking search request to ES")
        }
    }

    companion object : KLogging()
}