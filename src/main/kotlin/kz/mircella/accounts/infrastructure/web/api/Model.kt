package kz.mircella.accounts.infrastructure.web.api

import com.fasterxml.jackson.annotation.JsonCreator
import kz.mircella.accounts.domain.IndexOperationResult

class SearchQuery(val query: String)

class ApiResponse private constructor(val message: String) {

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromIndexOperationResult(result: IndexOperationResult) = ApiResponse(result.message)
    }
}