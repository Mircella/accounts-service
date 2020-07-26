package kz.mircella.accounts.infrastructure.http

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import kz.mircella.accounts.infrastructure.web.ErrorResponse
import org.apache.commons.io.IOUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import java.io.IOException
import java.nio.charset.StandardCharsets

class HttpErrorResponseExceptionFactory(private val mapper: ObjectMapper) {

    @Throws(IOException::class)
    fun create(httpResponse: ClientHttpResponse): HttpErrorResponseException {
        val status = httpResponse.statusCode
        val headers = httpResponse.headers
        val rawResponse: String = IOUtils.toString(httpResponse.body, StandardCharsets.UTF_8)
        return when {
            status.is5xxServerError -> {
                parse(rawResponse)
                        ?.let { response: ErrorResponse? -> HttpServerErrorResponseException(status, headers, response, rawResponse) }
                        ?: HttpServerErrorResponseException(status, headers, rawResponse)
            }
            status.is4xxClientError -> {
                parse(rawResponse)
                        ?.let { response: ErrorResponse? -> HttpClientErrorResponseException(status, headers, response, rawResponse) }
                        ?: HttpClientErrorResponseException(status, headers, rawResponse)
            }
            else -> {
                throw InvalidErrorStatus()
            }
        }
    }

    @Throws(IOException::class)
    private fun parse(rawResponse: String): ErrorResponse? {
        return try {
            val response: ErrorResponse = mapper.readValue(rawResponse, ErrorResponse::class.java)
            response
        } catch (exception: JsonProcessingException) {
            null
        }
    }

    abstract class HttpErrorResponseException internal constructor(
            private val status: HttpStatus, private val response: ErrorResponse?, private val rawResponse: String, private val headers: HttpHeaders, message: String?
    ) : RuntimeException(message) {

        companion object {
            fun removeNewlines(rawResponse: String): String {
                return rawResponse.replace("[\\r\\n]+".toRegex(), "")
            }
        }

    }

    class HttpClientErrorResponseException : HttpErrorResponseException {
        constructor(
                status: HttpStatus, headers: HttpHeaders, rawResponse: String
        ) : super(status, null, rawResponse, headers, buildMessage(status, rawResponse))

        constructor(
                status: HttpStatus, headers: HttpHeaders, response: ErrorResponse?, rawResponse: String
        ) : super(status, response, rawResponse, headers, buildMessage(status, rawResponse))

        companion object {
            private fun buildMessage(status: HttpStatus, rawResponse: String): String {
                return "HTTP client error occurred: [" + status.value() + "]: " + removeNewlines(rawResponse)
            }
        }
    }

    class HttpServerErrorResponseException : HttpErrorResponseException {
        constructor(
                status: HttpStatus, headers: HttpHeaders, rawResponse: String
        ) : super(status, null, rawResponse, headers, buildMessage(status, rawResponse))

        constructor(
                status: HttpStatus, headers: HttpHeaders, response: ErrorResponse?, rawResponse: String
        ) : super(status, response, rawResponse, headers, buildMessage(status, rawResponse))

        companion object {
            private fun buildMessage(status: HttpStatus, rawResponse: String): String {
                return "HTTP server error occurred: [" + status.value() + "]: " + removeNewlines(rawResponse)
            }
        }
    }
}
