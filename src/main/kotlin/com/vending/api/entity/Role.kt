package com.vending.api.entity

import com.vending.api.entity.enumeration.RoleType
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "authority")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class Role : Serializable {
    constructor()
    constructor(id: Int, name: RoleType?) {
        this.id = id
        this.name = name
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private var id = 0

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, length = 50)
    private var name: RoleType? = null
    fun getName(): RoleType? {
        return name
    }

    // prettier-ignore
    override fun toString(): String {
        return "Authority{" +
                "name='" + name + '\'' +
                "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}