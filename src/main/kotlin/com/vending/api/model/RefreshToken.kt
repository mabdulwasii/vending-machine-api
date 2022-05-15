package com.vending.api.model

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "refresh_token", indexes = [Index(columnList = "token", unique = true)])
class RefreshToken {
    constructor(id: Long, user: User?, token: String?, expiryDate: Instant?) {
        this.id = id
        this.user = user
        this.token = token
        this.expiryDate = expiryDate
    }

    constructor()

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
        private set

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var user: User? = null

    @Column(nullable = false, unique = true)
    var token: String? = null

    @Column(nullable = false)
    var expiryDate: Instant? = null
    override fun toString(): String {
        return "RefreshToken{" +
                "id=" + id +
                ", user=" + user +
                ", token='" + token + '\'' +
                ", expiryDate=" + expiryDate +
                '}'
    }
}