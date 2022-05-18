package com.vending.api.utils

import com.vending.api.dto.ApiResponse
import org.springframework.http.HttpStatus

class ApiResponseUtils {
    companion object {
        fun buildSuccessfulApiResponse(data: Any?, message: String, status: HttpStatus) = ApiResponse(
            error = false,
            message = message,
            data = data,
            status = status
        )

        fun buildSuccessfulApiResponse(data: Any?, message: String) = ApiResponse(
            error = false,
            message = message,
            data = data,
            status = HttpStatus.OK
        )
    }
}