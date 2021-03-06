package com.vending.api.security.auth

import com.vending.api.security.service.DomainUserDetailsService
import com.vending.api.utils.JWTUtils
import java.io.IOException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthTokenFilter(
    private val jwtUtils: JWTUtils,
    private val userDetailsService: DomainUserDetailsService
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.info("Entering doFilterInternal")
        try {
            val jwt = parseJwt(httpServletRequest)
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                val username: String = jwtUtils.getUserNameFromJwtToken(jwt)
                val userDetails: UserDetails = userDetailsService.loadUserByUsername(username)
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            logger.error("Cannot set user authentication: {}", e)
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse)
    }

    private fun parseJwt(request: HttpServletRequest): String? {
        val headerAuth: String? = request.getHeader("Authorization")
       return if (headerAuth?.isNotEmpty() == true && headerAuth.startsWith("Bearer ")) {
           headerAuth.substring(7)
        } else return headerAuth
    }
}