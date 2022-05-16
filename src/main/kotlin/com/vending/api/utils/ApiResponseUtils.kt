package com.vending.api.utils

import com.vending.api.dto.ApiResponse
import org.springframework.http.HttpStatus

class ApiResponseUtils {
    companion object {
        fun buildSuccessfulApiResponse(save: Any, message: String) = ApiResponse(
            error = false,
            message = message,
            data = save,
            status = HttpStatus.CREATED
        )
    }
}