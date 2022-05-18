package com.vending.api.utils

import com.vending.api.exception.PasswordMismatchException
import com.vending.api.utils.ConstantUtils.PASSWORD_MISMATCH

class SecurityUtils {
    companion object{
        fun ensurePasswordMatch(password: String, confirmPassword: String) {
            if (password != confirmPassword) throw PasswordMismatchException(PASSWORD_MISMATCH)
        }
    }

}
