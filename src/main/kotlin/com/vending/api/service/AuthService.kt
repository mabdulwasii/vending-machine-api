package com.vending.api.service

import com.vending.api.dto.Jwt
import com.vending.api.dto.LoginDetails
import com.vending.api.dto.RefreshTokenRequest
import com.vending.api.dto.RefreshTokenResponse
import com.vending.api.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthService(private val userRepository: UserRepository) {
    fun authenticate(loginDetails: LoginDetails): Jwt {
        TODO("Not yet implemented")
    }

    fun refreshToken(refreshTokenRequest: RefreshTokenRequest): RefreshTokenResponse {
        TODO("Not yet implemented")
    }

}
