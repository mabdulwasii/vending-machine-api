package com.vending.api.utils

import com.vending.api.dto.UserDetailsImpl
import com.vending.api.exception.GenericException
import org.springframework.security.core.context.SecurityContextHolder

class LoginUserUtils {
    companion object {
        fun getAuthUserId(): String {
            val authentication = SecurityContextHolder.getContext().authentication
            authentication?.let {
                val userDetails: UserDetailsImpl = it.principal as UserDetailsImpl
                return userDetails.username
            } ?: throw GenericException("Invalid user, please login")
        }

        fun ensureSellerIdMatches(sellerId: String, username: String) {
            if (!sellerId.equals(username, true)){
                throw GenericException("Access denied, operation cannot be performed by you")
            }
        }
    }
}