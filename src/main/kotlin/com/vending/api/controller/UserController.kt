package com.vending.api.controller

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.CreateUserRequest
import com.vending.api.dto.UserDto
import com.vending.api.exception.GenericException
import com.vending.api.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("api")
class UserController(private val userService: UserService) {
    @PostMapping("/user")
    suspend fun createUser(@RequestBody @Valid request: CreateUserRequest): ResponseEntity<ApiResponse> {
        val response = userService.createUser(request)
        return ResponseEntity(response, response.status)
    }

    @PutMapping("/user")
    suspend fun updateUser(@RequestBody @Valid userDto: UserDto): ResponseEntity<ApiResponse> {
        try {
            userDto.id!!
        } catch (exception: NullPointerException) {
            throw GenericException("Id cannot be null")
        }
        val response = userService.updateUser(userDto)
        return ResponseEntity(response, response.status)
    }

}