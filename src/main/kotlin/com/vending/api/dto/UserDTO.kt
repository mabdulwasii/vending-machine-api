package com.vending.api.dto

data class UserDTO(
    var id: Long? = null,
    var username: String,
    var deposit: Int,
    var roles: MutableSet<String> = HashSet()
)
