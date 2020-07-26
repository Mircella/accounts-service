package kz.mircella.accounts.infrastructure.elasticsearch

import com.fasterxml.jackson.databind.ObjectMapper
import kz.mircella.accounts.domain.IdentifiableObject
import kz.mircella.accounts.infrastructure.exceptions.ElasticSearchError
import mu.KLogging
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.SearchHit
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class ElasticSearchResultExtractor(val objectMapper: ObjectMapper) {

    fun <T> parseSearchResponse(searchResponse: SearchResponse, clazz: Class<T>): List<T> {
        val searchHits = searchResponse.hits.hits
        return try {
            searchHits.map {
                parseSearchHit(it, clazz)
            }
        } catch (e: ElasticSearchError) {
            emptyList()
        }
    }

    fun objectAsJson(identifiableObject: IdentifiableObject): String = objectMapper.writeValueAsString(identifiableObject)

    private fun <T> parseSearchHit(searchHit: SearchHit, clazz: Class<T>): T {
        val searchResponse = searchHit.sourceAsString
        return try {
            objectMapper.readValue(searchResponse, clazz)
        } catch (e: IOException) {
            logger.warn("Failed to parse search response: '$searchResponse', reason: ${e.localizedMessage}")
            throw ElasticSearchError("Failed to parse search response: '$searchResponse'")
        }
    }

    companion object : KLogging()
}