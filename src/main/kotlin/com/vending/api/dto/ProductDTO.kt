package com.vending.api.dto

import com.vending.api.utils.validation.MultiplesOfFive
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class ProductDTO(
    var id: Long? = null,
    @NotNull(message = "Amount available is required")
    var amountAvailable: Int,
    @NotNull(message = "Cost is required")
    @MultiplesOfFive
    var cost: Int,
    @NotEmpty(message = "Product name is required")
    var productName: String,
    @NotNull
    var sellerId: String
)
