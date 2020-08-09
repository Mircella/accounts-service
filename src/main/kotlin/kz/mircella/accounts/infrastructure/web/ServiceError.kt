package kz.mircella.accounts.infrastructure.web

import org.springframework.http.HttpStatus

enum class ServiceError(val status: HttpStatus, val code: Code) {

    BAD_REQUEST(
            HttpStatus.BAD_REQUEST,
            Code.INVALID_REQUEST
    ),
    INDEX_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            Code.INDEX_ALREADY_EXISTS
    ),
    INDEX_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            Code.INDEX_NOT_FOUND
    ),
    NOT_FOUND(
            HttpStatus.NOT_FOUND,
            Code.NOT_FOUND
    ),
    LOGIN_ALREADY_EXISTS(
            HttpStatus.BAD_REQUEST,
            Code.LOGIN_ALREADY_EXISTS
    ),
    INTERNAL_SYSTEM_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            Code.INTERNAL_ERROR
    );

    enum class Code(val value: String) {
        INTERNAL_ERROR("InternalError"),
        INDEX_ALREADY_EXISTS("IndexAlreadyExists"),
        INDEX_NOT_FOUND("IndexNotFound"),
        NOT_FOUND("NotFound"),
        INVALID_REQUEST("InvalidRequest"),
        LOGIN_ALREADY_EXISTS("LoginAlreadyExists");
    }

    data class DeveloperMessage(val message: String)
}
