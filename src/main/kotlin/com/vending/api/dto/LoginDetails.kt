package com.vending.api.dto

import com.vending.api.utils.password.ValidPassword
import javax.validation.constraints.NotEmpty

data class LoginDetails(
    @NotEmpty(message = "Username is required")
    private val username: String,
    @NotEmpty(message = "Username is required")
    @ValidPassword
    private val password: String
    )