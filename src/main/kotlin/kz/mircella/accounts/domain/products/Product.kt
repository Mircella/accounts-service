package kz.mircella.accounts.domain.products

import kz.mircella.accounts.domain.IdentifiableObject
import java.util.UUID

data class Product(
        val id: UUID,
        val name: String,
        val description: String,
        val proteins: Double,
        val carbonates: Double,
        val fetts: Double,
        val energyValue: Double
) : IdentifiableObject {
    override fun getId(): String = id.toString()

    fun toDetails() = ProductDetails(name)
}

data class ProductDetails(val name: String)