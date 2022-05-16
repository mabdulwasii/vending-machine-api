package com.vending.api.repository

import com.vending.api.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findOneWithAuthoritiesByUsernameIgnoreCase(username: String): User?
}