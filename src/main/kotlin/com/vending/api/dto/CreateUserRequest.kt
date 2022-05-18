package com.vending.api.dto

import com.vending.api.entity.enumeration.RoleType
import com.vending.api.utils.validation.ValidPassword
import javax.validation.constraints.NotEmpty

data class CreateUserRequest(
    @NotEmpty(message = "Username cannot be empty")
    val username: String,

    @ValidPassword
    var password: String,

    @NotEmpty(message = "Confirm password cannot be empty")
    val confirmPassword: String,

    @NotEmpty(message = "User role cannot be empty")
    val role: RoleType
)