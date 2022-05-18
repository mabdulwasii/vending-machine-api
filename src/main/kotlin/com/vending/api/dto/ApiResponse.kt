package com.vending.api.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.http.HttpStatus

data class ApiResponse(
    val error: Boolean,
    val message: String,
    val data: Any?,
    @JsonIgnore
    val status: HttpStatus
)
