package com.vending.api.service

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.LoginDetails
import com.vending.api.dto.RefreshTokenRequest
import com.vending.api.dto.RefreshTokenResponse

interface AuthService {
    @Throws(Exception::class)
    fun authenticate(loginDetails: LoginDetails): ApiResponse
    fun refreshToken(request: RefreshTokenRequest): RefreshTokenResponse
}