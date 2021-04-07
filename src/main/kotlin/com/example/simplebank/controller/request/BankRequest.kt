package com.example.simplebank.controller.request

import javax.validation.constraints.*

data class BankRequest(
    @field:Positive
    var accountNumber: Int,

    @field:PositiveOrZero
    var balance: Double,

    @field:Positive
    val customerId: Int,
)