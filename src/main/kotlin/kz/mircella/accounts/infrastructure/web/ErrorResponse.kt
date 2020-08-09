package kz.mircella.accounts.infrastructure.web

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonUnwrapped
import kz.mircella.accounts.infrastructure.web.ServiceError.Code
import kz.mircella.accounts.infrastructure.web.ServiceError.DeveloperMessage
import org.springframework.http.HttpStatus

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class ErrorResponse(val status: HttpStatus, val code: Code, @JsonUnwrapped val developerMessage: DeveloperMessage) {

    class Builder {

        private lateinit var statusCode: HttpStatus
        private lateinit var developerMessage: DeveloperMessage
        private lateinit var code: Code

        fun withDeveloperMessage(developerMessage: DeveloperMessage): Builder {
            this.developerMessage = developerMessage
            return this
        }

        fun withCode(code: Code): Builder {
            this.code = code
            return this
        }

        fun withStatusCode(statusCode: HttpStatus): Builder {
            this.statusCode = statusCode
            return this
        }

        fun build(): ErrorResponse {
            return ErrorResponse(statusCode, code, developerMessage)
        }
    }

    companion object {

        fun builder(): Builder {
            return Builder()
        }
    }
}