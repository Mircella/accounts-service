package kz.mircella.accounts.domain.receipts

interface ReceiptQueryRepository {

    fun findByQuery(query: String, from: Int, size: Int): List<Receipt>
    fun findByTitle(title: String): Receipt?
    fun findByAuthor(authorName: String): Receipt?
    fun findAll(size: Int?): List<Receipt>
}