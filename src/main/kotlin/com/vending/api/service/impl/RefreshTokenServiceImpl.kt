package com.vending.api.service.impl

import com.vending.api.entity.RefreshToken
import com.vending.api.exception.TokenRefreshExpiredException
import com.vending.api.repository.RefreshTokenRepository
import com.vending.api.repository.UserRepository
import com.vending.api.service.RefreshTokenService
import com.vending.api.utils.JWTUtils
import java.time.Instant
import java.util.Optional
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class RefreshTokenServiceImpl(
    refreshTokenRepository: RefreshTokenRepository,
    userRepository: UserRepository,
    jwtUtils: JWTUtils
) : RefreshTokenService {
    private val refreshTokenRepository: RefreshTokenRepository
    private val userRepository: UserRepository
    private val jwtUtils: JWTUtils

    init {
        this.jwtUtils = jwtUtils
        this.refreshTokenRepository = refreshTokenRepository
        this.userRepository = userRepository
    }

    override fun createRefreshToken(id: Long): Optional<RefreshToken> {
        var refreshToken: RefreshToken? = null
        val optionalUser = userRepository.findById(id)
        if (optionalUser.isPresent) {
            refreshToken = RefreshToken(
                user = optionalUser.get(),
                token = UUID.randomUUID().toString(),
                expiryDate = Instant.now().plusMillis(jwtUtils.getRefreshExpiration())
            )
            refreshToken = refreshTokenRepository.save(refreshToken)
        }
        return Optional.ofNullable<RefreshToken>(refreshToken)
    }

    override fun findByToken(refreshToken: String?): Optional<RefreshToken> {
        return refreshTokenRepository.findByToken(refreshToken)
    }

    override fun verifyExpiration(refreshToken: RefreshToken): RefreshToken {
        if (refreshToken.expiryDate < Instant.now()) {
            refreshTokenRepository.delete(refreshToken)
            throw TokenRefreshExpiredException(
                refreshToken.token,
                "Refresh token has expired. Please login again"
            )
        }
        return refreshToken
    }
}