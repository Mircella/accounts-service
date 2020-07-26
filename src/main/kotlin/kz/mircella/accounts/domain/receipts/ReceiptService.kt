package kz.mircella.accounts.domain.receipts

import kz.mircella.accounts.domain.IndexOperationResult
import kz.mircella.accounts.domain.products.ProductDetails
import kz.mircella.accounts.domain.user.UserDetails

interface ReceiptService {

    fun saveReceipt(command: CreateReceiptCommand): IndexOperationResult
    fun getAllReceipts(size: Int?): List<ReceiptDetails>
    fun findByQuery(query: String, size: Int? = 3): List<ReceiptDetails>
    fun findByTitle(title: String): ReceiptDetails
    fun createReceiptsIndex(): IndexOperationResult
    fun deleteReceiptsIndex(): IndexOperationResult
    fun deleteAllReceipts(): IndexOperationResult
}

data class CreateReceiptCommand(
        val title: String,
        val description: String,
        val author: UserDetails,
        val products: List<ProductDetails>
)
