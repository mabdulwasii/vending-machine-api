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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserService(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val passwordEncoder: PasswordEncoder
) {
    fun save(user: User) = userRepository.save(user)
    suspend fun createUser(request: CreateUserRequest): ApiResponse {
        SecurityUtils.ensurePasswordMatch(request.password, request.confirmPassword)
        val roles = setOf<Role>()
        var encodedPassword: String? = null
        withContext(Dispatchers.IO) {
            encodedPassword = passwordEncoder.encode(request.password)
            roleRepository.findByName(request.role)
        }?.let { roles.plus(it) } ?: throw InvalidRoleException("Invalid user role supplied")
        request.password = encodedPassword!!
        val user = transformCreateUserRequestToUserEntity(request)
        return buildSuccessfulApiResponse(save(user), Constant.USER_CREATED_SUCCESSFULLY, HttpStatus.CREATED)
    }

}