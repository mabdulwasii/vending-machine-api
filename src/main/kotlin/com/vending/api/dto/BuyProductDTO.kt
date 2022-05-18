package com.vending.api.dto

import javax.validation.constraints.NotNull

data class BuyProductDTO (
    @NotNull(message = "product id is required")
    val productId: Long,
    @NotNull(message = "amount of product is required")
    val amountOfProducts : Int
)
