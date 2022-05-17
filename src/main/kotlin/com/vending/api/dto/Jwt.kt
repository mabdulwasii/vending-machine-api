package com.vending.api.dto

data class Jwt(
    val accessToken: String,
    val id: Int,
    val username: String,
    val roles: List<String>,
    val type: String = "Bearer"
) {

}