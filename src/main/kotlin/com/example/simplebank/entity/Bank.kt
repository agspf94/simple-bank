package com.example.simplebank.entity

import com.example.simplebank.controller.request.BankRequest
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.Positive
import javax.validation.constraints.PositiveOrZero

@Entity(name = "bank")
data class Bank(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int?,

    @field:Positive
    var accountNumber: Int,

    @field:PositiveOrZero
    var balance: Double,

    @field:Positive
    val customerId: Int,
) {
    constructor(bank: BankRequest): this(
        null,
        bank.accountNumber,
        bank.balance,
        bank.customerId,
    )
}