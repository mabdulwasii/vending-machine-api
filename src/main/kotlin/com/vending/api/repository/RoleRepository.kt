package com.vending.api.repository

import com.vending.api.entity.Role
import com.vending.api.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<Role, Long> {
}