package com.vending.api.controller

import com.vending.api.dto.Jwt
import com.vending.api.dto.LoginDetails
import com.vending.api.dto.RefreshTokenRequest
import com.vending.api.dto.RefreshTokenResponse
import com.vending.api.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("api")
class AuthController(private val authService: AuthService) {
    @PostMapping("/authenticate")
    @Throws(Exception::class)
    fun authenticate(@RequestBody @Valid loginDetails: LoginDetails): ResponseEntity<*> {
        val token: Jwt = authService.authenticate(loginDetails)
        return ResponseEntity.ok(token)
    }

    @PostMapping("/refresh_token")
    fun refreshToken(@RequestBody @Valid refreshTokenRequest: RefreshTokenRequest): ResponseEntity<*> {
        val refreshTokenResponse: RefreshTokenResponse = authService.refreshToken(refreshTokenRequest)
        return ResponseEntity.ok(refreshTokenResponse)
    }
}