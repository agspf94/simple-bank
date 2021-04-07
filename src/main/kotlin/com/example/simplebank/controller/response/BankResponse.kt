package com.example.simplebank.controller.response

import com.example.simplebank.entity.Bank
import com.example.simplebank.entity.Customer
import java.io.Serializable

data class BankResponse(
    // From Bank
    val id: Int?,
    var accountNumber: Int,
    var balance: Double,

    // From Customer API
    val customerId: Int,
    var name: String,
    var email: String,
) : Serializable {
    constructor(bank: Bank, customer: Customer) : this(
        bank.id,
        bank.accountNumber,
        bank.balance,
        customer.customerId,
        customer.name,
        customer.email,
    )
}