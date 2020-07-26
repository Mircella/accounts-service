package kz.mircella.accounts.infrastructure.exceptions

import java.lang.RuntimeException

class UserNotFound(login: String): RuntimeException("User with login '$login' not found")

class ProductNotFound(name: String): RuntimeException("Product with name '$name' not found")

class ReceiptNotFound(title: String): RuntimeException("Receipt with title '$title' not found")