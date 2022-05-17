package com.vending.api.service

import com.vending.api.entity.RefreshToken
import java.util.Optional

interface RefreshTokenService {
    fun createRefreshToken(id: Long): Optional<RefreshToken>
    fun findByToken(refreshToken: String?): Optional<RefreshToken>
    fun verifyExpiration(refreshToken: RefreshToken): RefreshToken
}