package com.vending.api.dto

data class Jwt(
    val accessToken: String,
    val refreshToken: String,
    val id: Long,
    val username: String,
    val type: String = "Bearer"
) {

}