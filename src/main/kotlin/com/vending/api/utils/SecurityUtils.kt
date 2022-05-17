package com.vending.api.utils

import com.vending.api.exception.PasswordMismatchException

class SecurityUtils {
    companion object{
        fun ensurePasswordMatch(password: String, confirmPassword: String) {
            if (password != confirmPassword) throw PasswordMismatchException("Password mismatch")
        }
    }

}