package com.vending.api.utils

import com.vending.api.entity.enumeration.RoleType
import com.vending.api.exception.GenericException
import com.vending.api.exception.PasswordMismatchException
import com.vending.api.utils.ConstantUtils.PASSWORD_MISMATCH
import org.springframework.http.converter.HttpMessageNotReadableException

class SecurityUtils {
    companion object{
        fun ensurePasswordMatch(password: String, confirmPassword: String) {
            if (password != confirmPassword) throw PasswordMismatchException(PASSWORD_MISMATCH)
        }

        fun converRoleStringToEnum(role: String): RoleType {
            try {
                return RoleType.valueOf(role)
            }catch (exception : HttpMessageNotReadableException){
                throw GenericException("Invalid role supplied ($role)")
            }
        }
    }

}
