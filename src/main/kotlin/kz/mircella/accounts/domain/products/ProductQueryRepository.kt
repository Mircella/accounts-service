package kz.mircella.accounts.domain.products

interface ProductQueryRepository {
    fun findAll(size: Int?): List<Product>
    fun findByQuery(query: String, from: Int, size: Int): List<Product>
    fun findByName(name: String): Product?
}