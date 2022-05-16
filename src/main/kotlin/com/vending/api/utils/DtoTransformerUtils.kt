package com.vending.api.utils

import com.vending.api.dto.CreateUserRequest
import com.vending.api.entity.User

class DtoTransformerUtils {
    companion object {
        fun transformCreateUserRequestToUserEntity(request: CreateUserRequest) = User(
            username = request.username,
            password = request.password
        )
    }
}
