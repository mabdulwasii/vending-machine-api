package com.vending.api.service.impl

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.CreateUserRequest
import com.vending.api.dto.UserDto
import com.vending.api.entity.Role
import com.vending.api.entity.User
import com.vending.api.entity.enumeration.RoleType
import com.vending.api.repository.RefreshTokenRepository
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
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException


@Service
@Transactional
class UserServiceImpl(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val refreshTokenRepository: RefreshTokenRepository,
    val passwordEncoder: PasswordEncoder
) : UserService {
    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun save(user: User) = withContext(Dispatchers.IO) {
        userRepository.save(user)
    }

    override suspend fun createUser(request: CreateUserRequest): ApiResponse {
        SecurityUtils.ensurePasswordMatch(request.password, request.confirmPassword)
        val roles = mutableSetOf<Role>()
        var encodedPassword: String
        withContext(Dispatchers.IO) {
            encodedPassword = passwordEncoder.encode(request.password)
        }

        val role = withContext(Dispatchers.IO) {
            roleRepository.findByName(request.role)
        }
        role?.let { roles.add(it) }

        request.password = encodedPassword
        val user = transformCreateUserRequestToUserEntity(request)
        user.roles = roles
        val createdUser = withContext(Dispatchers.IO) {
            save(user)
        }
        val createdUserDto = transformUserEntityToUserDto(createdUser)
        return buildSuccessfulApiResponse(createdUserDto, Constant.USER_CREATED_SUCCESSFULLY, HttpStatus.CREATED)
    }

    override suspend fun updateUser(userDto: UserDto): ApiResponse {
        val user = userRepository.findByIdOrNull(userDto.id!!)
        val roles = mutableSetOf<Role>()
        user?.let {user ->
            userDto.username?.let { user.username = it }
            userDto.deposit?.let { user.deposit = it }
            userDto.roles.forEach { roleString ->
                val role = roleRepository.findByName(RoleType.valueOf(roleString))
                role?.let {
                    roles.add(it)
                }
            }
            log.info("Roles 000 {}", roles)
            user.roles.addAll(roles)
            val updatedUser= userRepository.save(user)
            return buildSuccessfulApiResponse(updatedUser, "User updated successfully")
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Could not retrieve user with the provided Id")
    }

    override suspend fun findAllUsers(): ApiResponse {
        val retrievedUsers = withContext(Dispatchers.IO) {
            userRepository.findAll()
        }
        return buildSuccessfulApiResponse(retrievedUsers, "success")
    }

    override suspend fun findUser(id: Long): ApiResponse {
        val user = userRepository.findByIdOrNull(id)
        return buildSuccessfulApiResponse(user, "User retrieved successfully")
    }

    @javax.transaction.Transactional
    override suspend fun deleteUser(id: Long): ApiResponse {
        withContext(Dispatchers.IO) {
            refreshTokenRepository.deleteByUserId(id)
            userRepository.deleteById(id)
        }
        return buildSuccessfulApiResponse(null, "", HttpStatus.NO_CONTENT)
    }

    override suspend fun getRoles(): ApiResponse {
        val allRoles = withContext(Dispatchers.IO) {
            roleRepository.findAll()
        }
        return buildSuccessfulApiResponse(allRoles, "Roles retrieved successfully")
    }

}