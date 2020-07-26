package kz.mircella.accounts.infrastructure.web.api

import com.fasterxml.jackson.annotation.JsonCreator
import kz.mircella.accounts.domain.products.CreateProductCommand
import kz.mircella.accounts.domain.products.Product
import kz.mircella.accounts.domain.products.ProductService
import kz.mircella.accounts.infrastructure.web.ControllerUtils.Companion.unwrappedAsync
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@RestController
class ProductsController(private val productService: ProductService) {

    @PostMapping("/products")
    fun insertProducts(@RequestBody products: List<CreateProductCommand>): CompletableFuture<ApiResponse> {
        return unwrappedAsync(Supplier {
            val result = productService.saveProducts(products)
            ApiResponse.fromIndexOperationResult(result)
        })
    }

    @GetMapping("/products")
    fun getProducts(
            @RequestParam(required = false, defaultValue = "10") size: Int
    ): CompletableFuture<ProductsResponse> {
        return unwrappedAsync(Supplier {
            val products = productService.getAllProducts(size).map { ProductResponse.fromProduct(it) }
            ProductsResponse(products)
        })
    }

    @GetMapping("/products/query")
    fun getProductsByQuery(
            @RequestParam(required = false, defaultValue = "10") size: Int,
            @RequestBody searchQuery: SearchQuery
    ): CompletableFuture<ProductsResponse> {
        return unwrappedAsync(Supplier {
            val products = productService.findByQuery(searchQuery.query).map { ProductResponse.fromProduct(it) }
            ProductsResponse(products)
        })
    }

    @GetMapping("/products/name")
    fun getProductsByName(
            @RequestParam(required = false, defaultValue = "10") size: Int,
            @RequestBody searchQuery: SearchQuery
    ): CompletableFuture<ProductResponse> {
        return unwrappedAsync(Supplier {
            val product = productService.findByName(searchQuery.query)
            ProductResponse.fromProduct(product)
        })
    }

    @PostMapping("/products/index/create")
    fun createProductsIndex(): CompletableFuture<ApiResponse> {
        return unwrappedAsync(Supplier {
            val result = productService.createProductsIndex()
            ApiResponse.fromIndexOperationResult(result)
        })
    }

    @DeleteMapping("/products/index/delete")
    fun deleteProductsIndex(): CompletableFuture<ApiResponse> {
        return unwrappedAsync(Supplier {
            val result = productService.deleteProductsIndex()
            ApiResponse.fromIndexOperationResult(result)
        })
    }

    @DeleteMapping("/products/index/clear")
    fun clearProductsIndex(): CompletableFuture<ApiResponse> {
        return unwrappedAsync(Supplier {
            val result = productService.deleteAllProducts()
            ApiResponse.fromIndexOperationResult(result)
        })
    }
}

data class ProductsResponse(val products: List<ProductResponse>)

data class ProductResponse private constructor(val name: String, val description: String) {

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromProduct(product: Product) = ProductResponse(product.name, product.description)
    }
}