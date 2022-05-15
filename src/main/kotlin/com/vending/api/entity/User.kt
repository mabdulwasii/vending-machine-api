package com.vending.api.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "user", uniqueConstraints = [UniqueConstraint(columnNames = ["username"])])
class User(
    @Id
    @GeneratedValue
    var id: Long? = null,

    @NotNull
    @Size(min = 5, max = 200)
    @Column(length = 200, unique = true, nullable = false)
    var username:String,

    @JsonIgnore
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "password", length = 256, nullable = false)
    var password: String,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: Set<Role> = HashSet()

)