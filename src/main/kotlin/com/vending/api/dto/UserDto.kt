package com.vending.api.dto

data class UserDto(
    var id: Long? = null,
    var username: String,
    var deposit: Int,
)
