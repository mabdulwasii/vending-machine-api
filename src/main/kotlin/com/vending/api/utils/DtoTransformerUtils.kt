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
            id = user.id,
            username = user.username,
            deposit = user.deposit,
            roles = user.roles.map { role -> role.name.name }.toSet()
        )
    }
}
