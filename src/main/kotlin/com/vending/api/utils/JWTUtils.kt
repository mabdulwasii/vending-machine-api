package com.vending.api.utils

import com.vending.api.dto.UserDetailsImpl
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.SignatureException
import io.jsonwebtoken.UnsupportedJwtException
import java.util.Date
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class JWTUtils {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${token.expiration}")
    private val expiration: Long = 0

    @Value("\${token.refreshExpiration}")
    private val refreshExpiration: Long = 0

    @Value("\${token.secret}")
    private val secret: String? = null

    fun generateJwtToken(authentication: Authentication): String? {
        val userPrincipal: UserDetailsImpl = authentication.principal as UserDetailsImpl
        log.info("userPrincipal generateJwtToken {}", userPrincipal)
        log.info("username generateJwtToken {}", userPrincipal.username)
        val claims: MutableMap<String, Any> = HashMap()
        claims["username"] = userPrincipal.username as Any
        claims["id"] = userPrincipal.id as Any
        claims["roles"] = userPrincipal.authorities as Any
        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .setClaims(claims)
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + expiration))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()
    }

    fun getUserNameFromJwtToken(token: String): String {
        log.info("Signing secret {}", secret)
        val username = Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .body.get("username",String::class.java)
        log.info("Username from token {}", username)
        return username
    }

    fun validateJwtToken(authToken: String?): Boolean {
        try {
            Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(authToken)
            return true
        } catch (e: SignatureException) {
            log.error("Invalid JWT signature: {}", e.message)
        } catch (e: MalformedJwtException) {
            log.error("Invalid JWT token: {}", e.message)
        } catch (e: ExpiredJwtException) {
            log.error("JWT token is expired: {}", e.message)
        } catch (e: UnsupportedJwtException) {
            log.error("JWT token is unsupported: {}", e.message)
        } catch (e: IllegalArgumentException) {
            log.error("JWT claims string is empty: {}", e.message)
        }
        log.info("Invalid jwt")
        return false
    }

    fun getRefreshExpiration(): Long {
        return refreshExpiration
    }
}