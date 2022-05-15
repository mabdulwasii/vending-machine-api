package com.vending.api.entity

import com.vending.api.entity.enumeration.RoleType
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "role")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class Role(
    @Id
    @GeneratedValue
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, length = 50)
    var name: RoleType
)