package kz.mircella.accounts.domain

interface IdentifiableObject {
    fun getId(): String
}

data class IndexOperationResult(val message: String)