package com.vending.api.service

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.LoginDetails
import com.vending.api.dto.RefreshTokenRequest

interface AuthService {
    @Throws(Exception::class)
    suspend fun authenticate(loginDetails: LoginDetails): ApiResponse
    suspend fun refreshToken(request: RefreshTokenRequest): ApiResponse
}