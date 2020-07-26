package kz.mircella.accounts.infrastructure.elasticsearch

import org.elasticsearch.common.unit.Fuzziness
import org.elasticsearch.index.query.*

object ElasticSearchQueryBuilder {

    fun <T> findByMoreLikeThisQuery(
            query: String, fields: Array<String>, from: Int, size: Int,
            search: (QueryBuilder, Int, Int) -> List<T>
    ): List<T> {
        val moreLikeThisQuery = createMoreLikeThisQuery(query, fields)
        return search(moreLikeThisQuery, from, size)
    }

    fun <T> findAllFieldsSatisfiedWithMatchPhraseQuery(
            query: String, fields: Array<String>, from: Int, size: Int,
            search: (QueryBuilder, Int, Int) -> List<T>
    ): List<T> {
        val multiMatchPhrasePrefixQuery = createMultiMatchPhrasePrefixQuery(query, fields)
        return search(multiMatchPhrasePrefixQuery, from, size)
    }

    fun <T> findAllFieldsSatisfiedWithMultiMatchQuery(
            query: String, fields: Array<String>, from: Int, size: Int,
            search: (QueryBuilder, Int, Int) -> List<T>
    ): List<T> {
        val multiMatchQuery = createMultiMatchQuery(query, fields)
        return search(multiMatchQuery, from, size)
    }

    fun createMatchQuery(query: String, field: String): QueryBuilder {
        return MatchQueryBuilder(field, query)
    }

    private fun createMoreLikeThisQuery(query: String, fields: Array<String>): QueryBuilder {
        val queries = query.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return QueryBuilders.moreLikeThisQuery(fields, queries, null)
                .minDocFreq(1)
                .minTermFreq(1)
                .maxDocFreq(10)
    }

    private fun createMultiMatchQuery(query: String, fields: Array<String>): QueryBuilder {
        val queries = query.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val multiMatchQueries = queries.map {
            MultiMatchQueryBuilder(it, *fields)
                    .fuzziness(Fuzziness.TWO)
                    .type(MultiMatchQueryBuilder.DEFAULT_TYPE)
                    .prefixLength(2)
        }
        return BoolQueryBuilder().appendMust(multiMatchQueries)
    }

    private fun createMultiMatchPhrasePrefixQuery(query: String, fields: Array<String>): QueryBuilder {
        val queries = query.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val boolQueries = queries.map { q ->
            val builder = BoolQueryBuilder()
            val multiMatchPhrasePrefixQueries = fields.map { f ->
                MatchPhrasePrefixQueryBuilder(f, q)
            }
            builder.appendShould(multiMatchPhrasePrefixQueries)
        }
        return BoolQueryBuilder().appendMust(boolQueries)
    }

    private fun BoolQueryBuilder.appendShould(queries: List<QueryBuilder>): BoolQueryBuilder {
        queries.forEach {
            this.should(it)
        }
        return this
    }

    private fun BoolQueryBuilder.appendMust(queries: List<QueryBuilder>): BoolQueryBuilder {
        queries.forEach {
            this.must(it)
        }
        return this
    }
}