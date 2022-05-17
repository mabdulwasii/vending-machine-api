package com.vending.api.utils

import com.vending.api.dto.CreateUserRequest
import com.vending.api.dto.UserDto
import com.vending.api.entity.User

class DtoTransformerUtils {
    companion object {
        fun transformCreateUserRequestToUserEntity(request: CreateUserRequest) = User(
            username = request.username,
            password = request.password
        )

        fun transformUserEntityToUserDto(user: User) = UserDto(
            user.id,
            user.username,
            user.deposit
        )
    }
}
