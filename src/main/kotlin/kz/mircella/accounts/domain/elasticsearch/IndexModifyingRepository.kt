package kz.mircella.accounts.domain.elasticsearch

import kz.mircella.accounts.domain.IdentifiableObject

interface IndexModifyingRepository {

    fun <T> createIndex(indexName: String, typeName: String, clazz: Class<T>)
    fun indexExists(indexName: String): Boolean
    fun <T> clearIndex(indexName: String, typeName: String, clazz: Class<T>)
    fun insert(indexName: String, typeName: String, objects: List<IdentifiableObject>)
    fun insert(indexName: String, typeName: String, obj: IdentifiableObject)
    fun deleteIndex(indexName: String)
}