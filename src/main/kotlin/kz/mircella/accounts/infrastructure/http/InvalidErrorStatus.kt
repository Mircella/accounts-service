package kz.mircella.accounts.infrastructure.http

internal class InvalidErrorStatus : RuntimeException("Trying to build an HTTP exception for non-error status.")
