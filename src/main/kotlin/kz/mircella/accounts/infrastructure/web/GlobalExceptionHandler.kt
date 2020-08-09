package kz.mircella.accounts.infrastructure.web

import kz.mircella.accounts.domain.accounts.Account
import kz.mircella.accounts.infrastructure.accounts.AccountNotFound
import kz.mircella.accounts.infrastructure.accounts.AccountWithSuchLoginAlreadyExists
import kz.mircella.accounts.infrastructure.exceptions.ESIndexAlreadyExists
import kz.mircella.accounts.infrastructure.exceptions.ESIndexNotFound
import kz.mircella.accounts.infrastructure.exceptions.ElasticSearchError
import kz.mircella.accounts.infrastructure.exceptions.UserNotFound
import mu.KLogging
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import java.util.HashMap
import javax.servlet.http.HttpServletResponse

@ControllerAdvice
class GlobalExceptionHandler {
    private val exceptionMappings = HashMap<Class<*>, ServiceError>()

    init {
        createInitialMappings()
    }

    private fun createInitialMappings() {
        registerMapping(ElasticSearchError::class.java, ServiceError.BAD_REQUEST)
        registerMapping(ESIndexNotFound::class.java, ServiceError.INDEX_NOT_FOUND)
        registerMapping(ESIndexAlreadyExists::class.java, ServiceError.INDEX_ALREADY_EXISTS)
        registerMapping(UserNotFound::class.java, ServiceError.NOT_FOUND)
        registerMapping(AccountNotFound::class.java, ServiceError.NOT_FOUND)
        registerMapping(AccountWithSuchLoginAlreadyExists::class.java, ServiceError.LOGIN_ALREADY_EXISTS)
    }

    @ExceptionHandler(Exception::class)
    @ResponseBody
    fun handleThrowable(ex: Exception, response: HttpServletResponse): ErrorResponse {
        val error = exceptionMappings.getOrDefault(ex.javaClass, ServiceError.INTERNAL_SYSTEM_ERROR)
        response.status = error.status.value()
        if (isInternalError(ex)) {
            logger.error("{} ({}): {}", error.status, error.code, ex.message)
        }
        return ErrorResponse
                .builder()
                .withCode(error.code)
                .withDeveloperMessage(ServiceError.DeveloperMessage(ex.localizedMessage))
                .withStatusCode(error.status)
                .build()
    }

    private fun isInternalError(ex: Exception): Boolean {
        return !exceptionMappings.containsKey(ex.javaClass)
    }

    private fun registerMapping(clazz: Class<*>, error: ServiceError) {
        exceptionMappings[clazz] = error
    }

    companion object : KLogging()

}