package com.vending.api.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.vending.api.entity.User
import java.util.stream.Collectors
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(id: Long?, username: String, password: String, roles: List<GrantedAuthority>) : UserDetails {
    val id: Long? = null
    private val username: String? = null
    @JsonIgnore
    private val password: String? = null
    private val authorities: Collection<GrantedAuthority>? = null
    override fun getUsername(): String {
        return username!!
    }

    override fun getPassword(): String {
        return password!!
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities!!
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    companion object {
        fun build(user: User): UserDetailsImpl {
            val roles: List<GrantedAuthority> = user.roles.stream()
                .map { role -> SimpleGrantedAuthority(role.name.name) }
                .collect(Collectors.toList())
            return UserDetailsImpl(
                user.id,
                user.username,
                user.password,
                roles
            )
        }
    }
}