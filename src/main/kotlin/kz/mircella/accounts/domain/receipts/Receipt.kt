package kz.mircella.accounts.domain.receipts

import kz.mircella.accounts.domain.IdentifiableObject
import kz.mircella.accounts.domain.products.Product
import kz.mircella.accounts.domain.products.ProductDetails
import kz.mircella.accounts.domain.user.User
import kz.mircella.accounts.domain.user.UserDetails
import java.time.OffsetDateTime
import java.util.UUID

data class Receipt(
        val id: UUID,
        val title: String,
        val description: String,
        val createdAt: OffsetDateTime,
        val author: User,
        val products: List<Product>
) : IdentifiableObject {
    override fun getId(): String = id.toString()

    fun toDetails() = ReceiptDetails(title, description, createdAt, author.toDetails(), products.map { it.toDetails() })
}

data class ReceiptDetails(
        val title: String,
        val description: String,
        val createdAt: OffsetDateTime,
        val author: UserDetails,
        val products: List<ProductDetails>
)