package kz.mircella.accounts.infrastructure.web.api

import com.fasterxml.jackson.annotation.JsonCreator
import kz.mircella.accounts.domain.products.ProductDetails
import kz.mircella.accounts.domain.receipts.CreateReceiptCommand
import kz.mircella.accounts.domain.receipts.ReceiptDetails
import kz.mircella.accounts.domain.receipts.ReceiptService
import kz.mircella.accounts.domain.user.UserDetails
import kz.mircella.accounts.infrastructure.web.ControllerUtils.Companion.unwrappedAsync
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@RestController
class ReceiptsController(private val receiptService: ReceiptService) {

    @PostMapping("/receipts")
    fun insertReceipt(@RequestBody request: CreateReceiptRequest): CompletableFuture<ApiResponse> {
        return unwrappedAsync(Supplier {
            val result = receiptService.saveReceipt(request.toCommand())
            ApiResponse.fromIndexOperationResult(result)
        })
    }

    @GetMapping("/receipts")
    fun getReceipts(
            @RequestParam(required = false, defaultValue = "10") size: Int
    ): CompletableFuture<ReceiptsResponse> {
        return unwrappedAsync(Supplier {
            val receipts = receiptService.getAllReceipts(size).map { ReceiptResponse.fromReceiptDetails(it) }
            ReceiptsResponse(receipts)
        })
    }

    @GetMapping("/receipts/query")
    fun getReceiptsByQuery(
            @RequestParam(required = false, defaultValue = "10") size: Int,
            @RequestBody searchQuery: SearchQuery
    ): CompletableFuture<ReceiptsResponse> {
        return unwrappedAsync(Supplier {
            val receipts = receiptService.findByQuery(searchQuery.query).map { ReceiptResponse.fromReceiptDetails(it) }
            ReceiptsResponse(receipts)
        })
    }

    @GetMapping("/receipts/title")
    fun getReceiptsByTitle(
            @RequestParam(required = false, defaultValue = "10") size: Int,
            @RequestBody searchQuery: SearchQuery
    ): CompletableFuture<ReceiptResponse> {
        return unwrappedAsync(Supplier {
            val receipt = receiptService.findByTitle(searchQuery.query)
            ReceiptResponse.fromReceiptDetails(receipt)
        })
    }

    @PostMapping("/receipts/index/create")
    fun createReceiptsIndex(): CompletableFuture<ApiResponse> {
        return unwrappedAsync(Supplier {
            val result = receiptService.createReceiptsIndex()
            ApiResponse.fromIndexOperationResult(result)
        })
    }

    @DeleteMapping("/receipts/index/delete")
    fun deleteReceiptsIndex(): CompletableFuture<ApiResponse> {
        return unwrappedAsync(Supplier {
            val result = receiptService.deleteReceiptsIndex()
            ApiResponse.fromIndexOperationResult(result)
        })
    }

    @DeleteMapping("/receipts/index/clear")
    fun clearReceiptsIndex(): CompletableFuture<ApiResponse> {
        return unwrappedAsync(Supplier {
            val result = receiptService.deleteAllReceipts()
            ApiResponse.fromIndexOperationResult(result)
        })
    }
}

data class ReceiptsResponse(val receipts: List<ReceiptResponse>)

data class ReceiptResponse private constructor(
        val name: String,
        val description: String,
        val author: String,
        val products: List<String>,
        val createdAt: OffsetDateTime
) {

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromReceiptDetails(receipt: ReceiptDetails) = ReceiptResponse(
                receipt.title,
                receipt.description,
                receipt.author.login,
                receipt.products.map { it.name },
                receipt.createdAt
        )
    }
}

data class CreateReceiptRequest(
        val title: String,
        val description: String,
        val author: String,
        val products: List<String>
) {
    fun toCommand() = CreateReceiptCommand(title, description, UserDetails(author), products.map { ProductDetails(it) })
}