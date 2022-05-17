package com.vending.api.utils

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.CreateUserRequest
import com.vending.api.entity.User
import org.springframework.http.HttpStatus

class DtoTransformerUtils {
    companion object {
        fun transformCreateUserRequestToUserEntity(request: CreateUserRequest) = User(
            username = request.username,
            password = request.password
        )

        fun buildSuccessApiResponse(message: String, data: Any, status: HttpStatus) : ApiResponse {
            return ApiResponse(false, message, data, status)
        }
    }
}
