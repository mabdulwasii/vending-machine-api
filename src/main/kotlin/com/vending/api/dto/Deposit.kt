package com.vending.api.dto

import com.vending.api.utils.validation.ValidCentAmount

data class Deposit(
    @ValidCentAmount
    val amount: Int
)
