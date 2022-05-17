package com.vending.api.repository

import com.vending.api.entity.RefreshToken
import java.util.Optional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken?, Long?> {
    fun findByToken(token: String?): Optional<RefreshToken>
}