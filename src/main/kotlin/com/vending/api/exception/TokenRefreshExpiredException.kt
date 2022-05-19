package com.vending.api.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class TokenRefreshExpiredException(val token: String, override val message: String) : RuntimeException()
