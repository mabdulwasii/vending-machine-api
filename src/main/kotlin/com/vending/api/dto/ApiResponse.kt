package com.vending.api.dto

import org.springframework.http.HttpStatus

data class ApiResponse(
    val error: Boolean,
    val message: String,
    val data: Any,
    val status: HttpStatus
)
