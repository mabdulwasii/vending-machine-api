package com.vending.api.controller

import com.vending.api.dto.LoginDetails
import com.vending.api.dto.RefreshTokenRequest
import com.vending.api.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
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

    @PostMapping("/logout/all")
    suspend fun logoutAllSessions(session: HttpSession,
                                  request: HttpServletRequest,
                                  response: HttpServletResponse
    ): ResponseEntity<*> {
        val auth: Authentication? = SecurityContextHolder.getContext().authentication
        if (auth != null) {
            SecurityContextLogoutHandler().logout(request, response, auth)
        }

        return ResponseEntity.ok().body(null)
    }
}