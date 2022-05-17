package com.vending.api.controller

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.CreateUserRequest
import com.vending.api.service.impl.UserServiceImpl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("api")
class UserController(private val userServiceImpl: UserServiceImpl) {
    @PostMapping("/user")
    suspend fun createUser(@RequestBody @Valid request: CreateUserRequest) : ResponseEntity<ApiResponse>{
        val response = userServiceImpl.createUser(request)
        return ResponseEntity(response, response.status)
    }



}