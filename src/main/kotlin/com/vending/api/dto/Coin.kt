package com.vending.api.dto

import com.vending.api.utils.validation.ValidCentAmount

data class Coin(
    @ValidCentAmount
    val amount: Int
)
