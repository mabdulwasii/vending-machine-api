package com.vending.api.entity

import com.vending.api.utils.validate.MultiplesOfFive
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "product")
class Product(
    @Id
    @GeneratedValue
    var id: Long? = null,

    @NotNull
    @Column(name = "amount_available")
    var amountAvailable: Int = 0,

    @NotNull
    @MultiplesOfFive
    var cost: Int = 0,

    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "product_name", length = 256, nullable = false)
    var productName: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    var seller: User ,

)