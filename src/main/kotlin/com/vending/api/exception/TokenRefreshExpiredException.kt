package com.vending.api.exception

class TokenRefreshExpiredException(val token: String, override val message: String) : RuntimeException()
