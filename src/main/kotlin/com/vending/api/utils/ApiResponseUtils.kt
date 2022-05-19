package com.vending.api.utils

import com.vending.api.dto.ApiResponse
import org.springframework.http.HttpStatus

class ApiResponseUtils {
    companion object {
        fun buildSuccessApiResponse(data: Any?, message: String, status: HttpStatus) = ApiResponse(
            error = false,
            message = message,
            data = data,
            status = status
        )

        fun buildSuccessApiResponse(data: Any?, message: String) = ApiResponse(
            error = false,
            message = message,
            data = data,
            status = HttpStatus.OK
        )


        fun buildFailedApiResponse(data: Any?, message: String, status: HttpStatus) = ApiResponse(
            error = true,
            message = message,
            data = data,
            status = status
        )

        fun buildFailedApiResponse(data: Any?, message: String) = ApiResponse(
            error = true,
            message = message,
            data = data,
            status = HttpStatus.BAD_REQUEST
        )
    }
}