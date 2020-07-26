package kz.mircella.accounts.infrastructure.http

import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResponseErrorHandler

class RestTemplateResponseErrorHandler(val exceptionFactory: HttpErrorResponseExceptionFactory) : ResponseErrorHandler{
    override fun hasError(response: ClientHttpResponse): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleError(response: ClientHttpResponse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}