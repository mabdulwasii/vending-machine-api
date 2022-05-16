package com.vending.api.controller

import com.vending.api.dto.ApiResponse
import com.vending.api.dto.CreateUserRequest
import com.vending.api.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("ap/user")
class UserController(private val userService: UserService) {
    suspend fun createUser(@RequestBody @Valid request: CreateUserRequest) : ResponseEntity<ApiResponse>{
        return ResponseEntity
            .ok()
            .body(userService.createUser(request))
    }

}