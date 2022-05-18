package com.vending.api.service.impl

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.Jwt
import com.vending.api.dto.LoginDetails
import com.vending.api.dto.RefreshTokenRequest
import com.vending.api.dto.RefreshTokenResponse
import com.vending.api.dto.UserDetailsImpl
import com.vending.api.entity.RefreshToken
import com.vending.api.exception.GenericException
import com.vending.api.exception.TokenRefreshExpiredException
import com.vending.api.service.AuthService
import com.vending.api.service.RefreshTokenService
import com.vending.api.utils.ApiResponseUtils.Companion.buildSuccessApiResponse
import com.vending.api.utils.JWTUtils
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val authenticationManager: AuthenticationManager,
    private val refreshTokenService: RefreshTokenService,
    private val jwtUtils: JWTUtils
) : AuthService {
    @Throws(Exception::class)
    override suspend fun authenticate(loginDetails: LoginDetails): ApiResponse {
        val authentication: Authentication?

        try {
            authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginDetails.username, loginDetails.password)
            )
        } catch (exception: BadCredentialsException) {
            exception.printStackTrace()
            throw BadCredentialsException("Bad Credentials")
        } catch (exception: Exception) {
            exception.printStackTrace()
            throw Exception("User authentication failed")
        }

        authentication?.let { it ->
            SecurityContextHolder.getContext().authentication = it
            val jwtToken = jwtUtils.generateJwtToken(it)
            val loginUser = it.principal as UserDetailsImpl
            val refreshToken = refreshTokenService.createRefreshToken(loginUser.id)
                .map { it.token }
                .orElse("")
            val jwt = Jwt(jwtToken!!, refreshToken, loginUser.id!!, loginUser.username)
            return buildSuccessApiResponse(jwt, "Login successful", HttpStatus.OK)
        } ?: throw BadCredentialsException("Invalid login details")
    }

    override suspend fun refreshToken(request: RefreshTokenRequest): ApiResponse {
        val refreshTokenRequest = request.refreshToken
        var token: String? = null
        refreshTokenService.findByToken(refreshTokenRequest)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::user)
            .map {
                try {
                    token = jwtUtils.generateToken(UserDetailsImpl.build(it))!!
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    throw GenericException("Failed to generate new token")
                }
            }

        token?.let {
            val tokenResponse = RefreshTokenResponse(it, refreshTokenRequest)
            return buildSuccessApiResponse(tokenResponse, "Token refreshed successfully")
        } ?: throw TokenRefreshExpiredException(refreshTokenRequest, "Invalid refresh token")

    }
}
