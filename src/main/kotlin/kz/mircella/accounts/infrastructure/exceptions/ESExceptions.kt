package kz.mircella.accounts.infrastructure.exceptions

class ESIndexAlreadyExists (indexName: String): RuntimeException("Index '$indexName' already exists")

class ESIndexNotFound (indexName: String): RuntimeException("Index '$indexName' not found")

class ElasticSearchError(message: String) : RuntimeException(message)