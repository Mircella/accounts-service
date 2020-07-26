package kz.mircella.accounts.infrastructure.products

import kz.mircella.accounts.domain.IndexOperationResult
import kz.mircella.accounts.domain.elasticsearch.IndexModifyingRepository
import kz.mircella.accounts.domain.products.CreateProductCommand
import kz.mircella.accounts.domain.products.Product
import kz.mircella.accounts.domain.products.ProductQueryRepository
import kz.mircella.accounts.domain.products.ProductService
import kz.mircella.accounts.infrastructure.exceptions.ESIndexAlreadyExists
import kz.mircella.accounts.infrastructure.exceptions.ProductNotFound
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class ESProductService(
        private val productQueryRepository: ProductQueryRepository,
        private val indexModifyingRepository: IndexModifyingRepository
) : ProductService {

    override fun findByQuery(query: String, size: Int?): List<Product> {
        return productQueryRepository.findByQuery(query, 0, DEFAULT_SIZE)
    }

    override fun findByName(name: String): Product {
        return productQueryRepository.findByName(name) ?: throw ProductNotFound(name)
    }

    override fun createProductsIndex(): IndexOperationResult {
        if (indexModifyingRepository.indexExists(INDEX_NAME)) {
            throw ESIndexAlreadyExists(INDEX_NAME)
        }
        indexModifyingRepository.createIndex(INDEX_NAME, TYPE_NAME, Product::class.java)
        return IndexOperationResult("Index $INDEX_NAME was created successfully")
    }

    override fun deleteProductsIndex(): IndexOperationResult {
        indexModifyingRepository.deleteIndex(INDEX_NAME)
        return IndexOperationResult("Index $INDEX_NAME was deleted successfully")
    }

    override fun saveProducts(products: List<CreateProductCommand>): IndexOperationResult {
        val message = if (indexModifyingRepository.indexExists(INDEX_NAME)) {
            indexModifyingRepository.insert(INDEX_NAME, TYPE_NAME, products.map { it.toProduct() })
            "Products into $INDEX_NAME were inserted successfully"
        } else {
            "Index $INDEX_NAME does not exist"
        }
        return IndexOperationResult((message))
    }

    override fun getAllProducts(size: Int?): List<Product> {
        return productQueryRepository.findAll(size ?: DEFAULT_SIZE)
    }

    override fun deleteAllProducts(): IndexOperationResult {
        indexModifyingRepository.clearIndex(INDEX_NAME, TYPE_NAME, Product::class.java)
        return IndexOperationResult("Index $INDEX_NAME was cleared successfully")
    }

    companion object : KLogging() {
        private const val DEFAULT_SIZE = 10
        private const val INDEX_NAME = "products"
        private const val TYPE_NAME = "product"
    }
}

