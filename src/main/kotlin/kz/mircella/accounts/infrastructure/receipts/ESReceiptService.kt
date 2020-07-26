package kz.mircella.accounts.infrastructure.receipts

import kz.mircella.accounts.domain.receipts.ReceiptQueryRepository
import kz.mircella.accounts.domain.IndexOperationResult
import kz.mircella.accounts.domain.elasticsearch.IndexModifyingRepository
import kz.mircella.accounts.domain.products.Product
import kz.mircella.accounts.domain.products.ProductService
import kz.mircella.accounts.domain.receipts.*
import kz.mircella.accounts.domain.time.TimeProvider
import kz.mircella.accounts.domain.user.User
import kz.mircella.accounts.domain.user.UserService
import kz.mircella.accounts.infrastructure.exceptions.ESIndexAlreadyExists
import kz.mircella.accounts.infrastructure.exceptions.ReceiptNotFound
import mu.KLogging
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ESReceiptService(
        private val receiptQueryRepository: ReceiptQueryRepository,
        private val indexModifyingRepository: IndexModifyingRepository,
        private val userService: UserService,
        private val productService: ProductService,
        private val timeProvider: TimeProvider
) : ReceiptService {

    override fun findByQuery(query: String, size: Int?): List<ReceiptDetails> {
        return receiptQueryRepository.findByQuery(query, 0, DEFAULT_SIZE).map { it.toDetails() }
    }

    override fun findByTitle(title: String): ReceiptDetails {
        return receiptQueryRepository.findByTitle(title)?.toDetails() ?: throw ReceiptNotFound(title)
    }

    override fun saveReceipt(command: CreateReceiptCommand): IndexOperationResult {
        val message = if (indexModifyingRepository.indexExists(INDEX_NAME)) {
            val user = userService.findByLogin(command.author.login)
            val products = command.products.map { productService.findByName(it.name) }
            val receipt = createReceipt(command.title, command.description, user, products)
            indexModifyingRepository.insert(INDEX_NAME, TYPE_NAME, receipt)
            "Receipt '${command.title}'  was inserted successfully into $INDEX_NAME"
        } else {
            "Index $INDEX_NAME does not exist"
        }
        return IndexOperationResult((message))
    }

    override fun getAllReceipts(size: Int?): List<ReceiptDetails> {
        return receiptQueryRepository.findAll(size ?: DEFAULT_SIZE).map { it.toDetails() }
    }

    override fun createReceiptsIndex(): IndexOperationResult {
        if (indexModifyingRepository.indexExists(INDEX_NAME)) {
            throw ESIndexAlreadyExists(INDEX_NAME)
        }
        indexModifyingRepository.createIndex(INDEX_NAME, TYPE_NAME, Receipt::class.java)
        return IndexOperationResult("Index $INDEX_NAME was created successfully")
    }

    override fun deleteReceiptsIndex(): IndexOperationResult {
        indexModifyingRepository.deleteIndex(INDEX_NAME)
        return IndexOperationResult("Index $INDEX_NAME was deleted successfully")
    }

    override fun deleteAllReceipts(): IndexOperationResult {
        indexModifyingRepository.clearIndex(INDEX_NAME, TYPE_NAME, Receipt::class.java)
        return IndexOperationResult("Index $INDEX_NAME was cleared successfully")
    }

    private fun createReceipt(title: String, description: String, user: User, products: List<Product>) = Receipt(
            UUID.randomUUID(), title, description, timeProvider.now(), user, products
    )

    companion object : KLogging() {
        private const val DEFAULT_SIZE = 10
        private const val INDEX_NAME = "receipts"
        private const val TYPE_NAME = "receipt"
    }
}