package com.vending.api.service.impl

import com.vending.api.entity.RefreshToken
import com.vending.api.exception.TokenRefreshExpiredException
import com.vending.api.repository.RefreshTokenRepository
import com.vending.api.repository.UserRepository
import com.vending.api.service.RefreshTokenService
import com.vending.api.utils.ConstantUtils.INVALID_REFRESH_TOKEN
import com.vending.api.utils.JWTUtils
import java.time.Instant
import java.util.Optional
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    override suspend fun createRefreshToken(id: Long): Optional<RefreshToken> {
        lateinit var refreshToken: RefreshToken
        val optionalUser = withContext(Dispatchers.IO) {
            userRepository.findById(id)
        }
        if (optionalUser.isPresent) {
            refreshToken = RefreshToken(
                user = optionalUser.get(),
                token = UUID.randomUUID().toString(),
                expiryDate = Instant.now().plusMillis(jwtUtils.getRefreshExpiration())
            )
            refreshToken = withContext(Dispatchers.IO) {
                refreshTokenRepository.save(refreshToken)
            }
        }
        return Optional.of(refreshToken)
    }

    override suspend fun findByToken(refreshToken: String?): Optional<RefreshToken> {
        return withContext(Dispatchers.IO) {
            refreshTokenRepository.findByToken(refreshToken)
        }
    }

    override fun verifyExpiration(refreshToken: RefreshToken): RefreshToken {
        if (refreshToken.expiryDate < Instant.now()) {
            refreshTokenRepository.delete(refreshToken)
            throw TokenRefreshExpiredException(refreshToken.token, INVALID_REFRESH_TOKEN)
        }
        return refreshToken
    }
}