package com.vending.api.service

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.CreateUserRequest
import com.vending.api.dto.UserDto
import com.vending.api.entity.User

interface UserService {
    suspend fun save(user: User): User
    suspend fun createUser(request: CreateUserRequest): ApiResponse
    suspend fun updateUser(userDto: UserDto): ApiResponse
    suspend fun findAllUsers(): ApiResponse
    suspend fun findUser(id: Long): ApiResponse
    suspend fun deleteUser(id: Long): ApiResponse
    suspend fun getRoles(): ApiResponse
}