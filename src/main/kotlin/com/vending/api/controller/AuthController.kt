package com.vending.api.controller

import com.vending.api.dto.LoginDetails
import com.vending.api.dto.RefreshTokenRequest
import com.vending.api.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("api", consumes=["application/json"], produces = ["application/json"] )
class AuthController(private val authService: AuthService) {
    @PostMapping("/authenticate")
    @Throws(Exception::class)
    suspend fun authenticate(@RequestBody @Valid loginDetails: LoginDetails): ResponseEntity<*> {
        val response = authService.authenticate(loginDetails)
        return ResponseEntity(response, response.status)
    }

    @PostMapping("/refresh_token")
    suspend fun refreshToken(@RequestBody @Valid refreshTokenRequest: RefreshTokenRequest): ResponseEntity<*> {
        val response = authService.refreshToken(refreshTokenRequest)
        return ResponseEntity(response, response.status)
    }
}