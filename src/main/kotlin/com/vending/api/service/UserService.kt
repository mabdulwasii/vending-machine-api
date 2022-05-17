package com.vending.api.service

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.CreateUserRequest
import com.vending.api.entity.User

interface UserService {
    fun save(user: User): User
    suspend fun createUser(request: CreateUserRequest): ApiResponse
}