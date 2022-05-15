package com.vending.api.model

import org.springframework.data.jpa.domain.AbstractPersistable_.id
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
class RefreshToken (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var user: User,

    @Column(nullable = false, unique = true)
    var token: String,

    @Column(nullable = false)
    var expiryDate: Instant,
)