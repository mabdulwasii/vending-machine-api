package com.vending.api.security.service

import com.vending.api.dto.UserDetailsImpl
import com.vending.api.entity.User
import com.vending.api.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Authenticate a user from the database.
 */
@Service("userDetailsService")
class DomainUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun loadUserByUsername(username: String): UserDetailsImpl {
        log.debug("Authenticating {}", username)
        val loginUser: User? = userRepository.findOneWithAuthoritiesByUsernameIgnoreCase(username)
        loginUser?.let { return UserDetailsImpl.build(loginUser) }
            ?: throw UsernameNotFoundException("User $username was not found in the database")
    }
}