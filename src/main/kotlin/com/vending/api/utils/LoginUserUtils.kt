package com.vending.api.utils

import com.vending.api.exception.GenericException
import com.vending.api.exception.InvalidUserNameException
import com.vending.api.utils.ConstantUtils.ACCESS_DENIED
import com.vending.api.utils.ConstantUtils.INVALID_USER_PLEASE_LOGIN
import org.springframework.security.core.context.SecurityContextHolder

class LoginUserUtils {
    companion object {
        fun getAuthUserId(): String {
            val authentication = SecurityContextHolder.getContext().authentication
            authentication?.let {
                return it.name
            } ?: throw InvalidUserNameException(INVALID_USER_PLEASE_LOGIN)
        }

        fun ensureSellerIdMatches(sellerId: String, username: String) {
            if (!sellerId.equals(username, true)){
                throw GenericException(ACCESS_DENIED)
            }
        }
    }
}