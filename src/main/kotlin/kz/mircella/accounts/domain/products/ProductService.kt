package kz.mircella.accounts.domain.products

import kz.mircella.accounts.domain.IndexOperationResult

import java.util.UUID

interface ProductService {

    fun saveProducts(products: List<CreateProductCommand>): IndexOperationResult
    fun getAllProducts(size: Int?): List<Product>
    fun findByQuery(query: String, size: Int? = 3): List<Product>
    fun findByName(name: String): Product
    fun createProductsIndex(): IndexOperationResult
    fun deleteProductsIndex(): IndexOperationResult
    fun deleteAllProducts(): IndexOperationResult
}

data class CreateProductCommand(
        val name: String,
        val description: String,
        val proteins: Double,
        val carbonates: Double,
        val fetts: Double
) {
    fun toProduct() = Product(UUID.randomUUID(), name, description, proteins, carbonates, fetts, calculateEnergyValue())

    private fun calculateEnergyValue(): Double {
        return (proteins + carbonates) * 4 + fetts * 9
    }
}
