package com.example.simplebank.entity

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class Customer(
    val customerId: Int,
    var name: String,
    var email: String,
)