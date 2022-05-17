package com.vending.api.dto

import javax.validation.constraints.NotEmpty

data class RefreshTokenRequest (
    @NotEmpty(message = "Refresh token is required")
    private val refreshToken: String
)