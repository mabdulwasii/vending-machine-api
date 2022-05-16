package com.vending.api.service

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.CreateUserRequest
import com.vending.api.entity.Role
import com.vending.api.entity.User
import com.vending.api.exception.InvalidRoleException
import com.vending.api.repository.RoleRepository
import com.vending.api.repository.UserRepository
import com.vending.api.utils.ApiResponseUtils.Companion.buildSuccessfulApiResponse
import com.vending.api.utils.Constant
import com.vending.api.utils.DtoTransformerUtils.Companion.transformCreateUserRequestToUserEntity
import com.vending.api.utils.SecurityUtils
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserService(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val passwordEncoder: BCryptPasswordEncoder
) {

    fun save(user: User) = userRepository.save(user)
    fun createUser(request: CreateUserRequest): ApiResponse {
        SecurityUtils.ensurePasswordMatch(request.password, request.confirmPassword)
        val encodedPassword = passwordEncoder.encode(request.password)
        request.password = encodedPassword
        val roles = setOf<Role>()
        roleRepository.findByName(request.role)
            ?.let { roles.plus(it) } ?: throw InvalidRoleException("Invalid user role supplied")
        val user = transformCreateUserRequestToUserEntity(request)
        return buildSuccessfulApiResponse(save(user), Constant.USER_CREATED_SUCCESSFULLY)
    }

}