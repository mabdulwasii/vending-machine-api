package com.vending.api.dto

import com.vending.api.utils.validate.ValidPassword
import javax.validation.constraints.NotEmpty

data class LoginDetails(
    @NotEmpty(message = "Username is required")
    val username: String,
    @NotEmpty(message = "Username is required")
    @ValidPassword
    val password: String
)