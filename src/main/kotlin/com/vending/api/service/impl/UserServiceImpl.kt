package com.vending.api.service.impl

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.CreateUserRequest
import com.vending.api.dto.Deposit
import com.vending.api.dto.UserDTO
import com.vending.api.entity.Role
import com.vending.api.entity.User
import com.vending.api.entity.enumeration.RoleType
import com.vending.api.exception.InvalidUserNameException
import com.vending.api.exception.UserNameExistsException
import com.vending.api.repository.RefreshTokenRepository
import com.vending.api.repository.RoleRepository
import com.vending.api.repository.UserRepository
import com.vending.api.service.UserService
import com.vending.api.utils.ApiResponseUtils.Companion.buildFailedApiResponse
import com.vending.api.utils.ApiResponseUtils.Companion.buildSuccessApiResponse
import com.vending.api.utils.ConstantUtils
import com.vending.api.utils.ConstantUtils.COULD_NOT_RETRIEVE_LOGIN_USER
import com.vending.api.utils.ConstantUtils.COULD_NOT_RETRIEVE_USER
import com.vending.api.utils.ConstantUtils.DEPOSIT_RESET_SUCCESSFUL
import com.vending.api.utils.ConstantUtils.DEPOSIT_SUCCESSFUL
import com.vending.api.utils.ConstantUtils.INVALID_USER_PLEASE_LOGIN
import com.vending.api.utils.ConstantUtils.USERNAME_ALREADY_EXIST
import com.vending.api.utils.ConstantUtils.USER_UPDATED_SUCCESSFULLY
import com.vending.api.utils.DtoTransformerUtils.Companion.transformCreateUserRequestToUserEntity
import com.vending.api.utils.DtoTransformerUtils.Companion.transformUserEntityToUserDto
import com.vending.api.utils.LoginUserUtils
import com.vending.api.utils.SecurityUtils.Companion.converRoleStringToEnum
import com.vending.api.utils.SecurityUtils.Companion.ensurePasswordMatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    override suspend fun save(user: User) = withContext(Dispatchers.IO) {
        userRepository.save(user)
    }

    override suspend fun createUser(request: CreateUserRequest): ApiResponse {
        val roleType = converRoleStringToEnum(request.role)
        getUserByUsername(request.username)?.let {
            throw UserNameExistsException(USERNAME_ALREADY_EXIST)
        }
        ensurePasswordMatch(request.password, request.confirmPassword)
        val roles = mutableSetOf<Role>()
        var encodedPassword: String
        withContext(Dispatchers.IO) {
            encodedPassword = passwordEncoder.encode(request.password)
        }

        val role = withContext(Dispatchers.IO) {
            roleRepository.findByName(roleType)
        }
        role?.let { roles.add(it) }

        request.password = encodedPassword
        val user = transformCreateUserRequestToUserEntity(request)
        user.roles = roles
        val createdUser = withContext(Dispatchers.IO) {
            save(user)
        }
        val createdUserDto = transformUserEntityToUserDto(createdUser)
        return buildSuccessApiResponse(createdUserDto, ConstantUtils.USER_CREATED_SUCCESSFULLY, HttpStatus.CREATED)
    }

    override suspend fun updateUser(userDto: UserDTO): ApiResponse {
        val user = userRepository.findByIdOrNull(userDto.id)
        val roles = mutableSetOf<Role>()
        user?.let {user ->
            user.username = userDto.username
            user.deposit = userDto.deposit
            userDto.roles.map { roleString ->
                val role = roleRepository.findByName(RoleType.valueOf(roleString))
                role?.let {
                    roles.add(it)
                }
            }
            user.roles.addAll(roles)
            val updatedUser= userRepository.save(user)
            return buildSuccessApiResponse(updatedUser, USER_UPDATED_SUCCESSFULLY)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, COULD_NOT_RETRIEVE_USER)
    }

    override suspend fun findAllUsers(): ApiResponse {
        val retrievedUsers = withContext(Dispatchers.IO) {
            userRepository.findAll()
        }
        return buildSuccessApiResponse(retrievedUsers, "success")
    }

    override suspend fun findUser(id: Long): ApiResponse {
        val user = userRepository.findByIdOrNull(id)
        return buildSuccessApiResponse(user, "User retrieved successfully")
    }

    @javax.transaction.Transactional
    override suspend fun deleteUser(id: Long): ApiResponse {
        withContext(Dispatchers.IO) {
            refreshTokenRepository.deleteByUserId(id)
            userRepository.deleteById(id)
        }
        return buildSuccessApiResponse(null, "", HttpStatus.NO_CONTENT)
    }

    override suspend fun getRoles(): ApiResponse {
        val allRoles = withContext(Dispatchers.IO) {
            roleRepository.findAll()
        }
        return buildSuccessApiResponse(allRoles, "Roles retrieved successfully")
    }

    override suspend fun depositCoin(deposit: Deposit): ApiResponse {
        val currentUsername = LoginUserUtils.getAuthUserId()
        getUserByUsername(currentUsername)?.let {
            it.deposit += deposit.amount
            val updatedUserDeposit = userRepository.save(it)
            return buildSuccessApiResponse(updatedUserDeposit, DEPOSIT_SUCCESSFUL)
        }?: return buildFailedApiResponse(null, INVALID_USER_PLEASE_LOGIN)
    }

    override suspend fun resetDeposit(): ApiResponse {
        val currentUsername = LoginUserUtils.getAuthUserId()
        getUserByUsername(currentUsername)?.let {
            it.deposit = 0
            val updatedUser = userRepository.save(it)
            val userDto = transformUserEntityToUserDto(updatedUser)
            return buildFailedApiResponse(userDto, DEPOSIT_RESET_SUCCESSFUL)
        }?: throw InvalidUserNameException(COULD_NOT_RETRIEVE_LOGIN_USER)
    }

    override suspend fun getUserByUsername(currentUsername: String): User? {
        return withContext(Dispatchers.IO) {
            userRepository.findOneWithAuthoritiesByUsernameIgnoreCase(currentUsername)
        }
    }
}