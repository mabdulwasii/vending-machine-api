package com.vending.api.controller

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.CreateUserRequest
import com.vending.api.dto.Deposit
import com.vending.api.dto.UserDTO
import com.vending.api.exception.GenericException
import com.vending.api.service.UserService
import com.vending.api.utils.ConstantUtils.ID_NOT_NULL
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("api")
class UserController(private val userService: UserService) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("/user")
    suspend fun createUser(@RequestBody @Valid request: CreateUserRequest): ResponseEntity<ApiResponse> {
        log.debug("REST request to create User: {}", request)
        val response = userService.createUser(request)
        return ResponseEntity(response, response.status)
    }

    @PutMapping("/user")
    suspend fun updateUser(@RequestBody @Valid userDto: UserDTO): ResponseEntity<ApiResponse> {
        log.debug("REST request to update a User : {}", userDto)
        try {
            userDto.id!!
        } catch (exception: NullPointerException) {
            throw GenericException(ID_NOT_NULL)
        }
        val response = userService.updateUser(userDto)
        return ResponseEntity(response, response.status)
    }

    @GetMapping("/user/{id}")
    suspend fun findUser(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        log.debug("REST request to find a User with id: {}", id)
        val response = userService.findUser(id)
        return ResponseEntity(response, response.status)
    }

    @GetMapping("/user")
    suspend fun findAllUsers(): ResponseEntity<ApiResponse> {
        log.debug("REST request to find all Users: " )
        val response = userService.findAllUsers()
        return ResponseEntity(response, response.status)
    }

    /**
     * Gets a list of all roles.
     * @return a string list of all roles.
     */
    @GetMapping("/user/roles")
    suspend fun getRoles(): ResponseEntity<ApiResponse> {
        log.debug("REST request to fin all roles")
        val response = userService.getRoles()
        return ResponseEntity(response, response.status)
    }

    @DeleteMapping("/user/{id}")
    suspend fun deleteUser(@PathVariable id: Long): ResponseEntity<ApiResponse> {
        log.debug("REST request to delete a User with id: {}", id)
        val response = userService.deleteUser(id)
        return ResponseEntity(response, response.status)
    }

    @Secured("ROLE_BUYER")
    @PostMapping("/deposit")
    suspend fun depositCoin(@RequestBody @Valid deposit: Deposit): ResponseEntity<ApiResponse> {
        log.debug("REST request to deposit a coin into vending account: {}", deposit)
        val response = userService.depositCoin(deposit)
        return ResponseEntity(response, response.status)
    }

    @Secured("ROLE_BUYER")
    @PostMapping("/reset")
    suspend fun resetDeposit(): ResponseEntity<ApiResponse> {
        log.debug("REST request to reset deposit to zero")
        val response = userService.resetDeposit()
        return ResponseEntity(response, response.status)
    }

}