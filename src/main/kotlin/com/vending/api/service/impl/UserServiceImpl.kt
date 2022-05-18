package com.vending.api.service.impl

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.CreateUserRequest
import com.vending.api.dto.UserDto
import com.vending.api.entity.Role
import com.vending.api.entity.User
import com.vending.api.entity.enumeration.RoleType
import com.vending.api.exception.InvalidRoleException
import com.vending.api.repository.RoleRepository
import com.vending.api.repository.UserRepository
import com.vending.api.service.UserService
import com.vending.api.utils.ApiResponseUtils.Companion.buildSuccessfulApiResponse
import com.vending.api.utils.Constant
import com.vending.api.utils.DtoTransformerUtils.Companion.transformCreateUserRequestToUserEntity
import com.vending.api.utils.DtoTransformerUtils.Companion.transformUserEntityToUserDto
import com.vending.api.utils.SecurityUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import javax.transaction.Transactional


@Service
@Transactional
class UserServiceImpl(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val passwordEncoder: PasswordEncoder
) : UserService {
    override fun save(user: User) = userRepository.save(user)
    override suspend fun createUser(request: CreateUserRequest): ApiResponse {
        SecurityUtils.ensurePasswordMatch(request.password, request.confirmPassword)
        val roles = setOf<Role>()
        var encodedPassword: String? = null
        withContext(Dispatchers.IO) {
            encodedPassword = passwordEncoder.encode(request.password)
            roleRepository.findByName(request.role)
        }?.let { roles.plus(it) } ?: throw InvalidRoleException("Invalid user role supplied")
        request.password = encodedPassword!!
        val user = transformCreateUserRequestToUserEntity(request)
        val createdUser = withContext(Dispatchers.IO) {
            save(user)
        }
        val createdUserDto = transformUserEntityToUserDto(createdUser)
        return buildSuccessfulApiResponse(createdUserDto, Constant.USER_CREATED_SUCCESSFULLY, HttpStatus.CREATED)
    }

    override suspend fun updateUser(userDto: UserDto): ApiResponse {
        var retrievedUser: User? = null
        withContext(Dispatchers.IO) {
            userRepository.findById(userDto.id!!)
        }.map { user ->
                userDto.username?.let { user.username = it }
                userDto.deposit?.let { user.deposit = it }
                user.roles.clear()
                userDto.roles.map {
                    val role = roleRepository.findByName(RoleType.valueOf(it))
                    role?.let { user.roles.add(role) }
                }
                retrievedUser = user
            }
        retrievedUser?.let {
            return buildSuccessfulApiResponse(it, "User updated successfully")
        }?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Could not retrieve user with the provided Id")
    }

}