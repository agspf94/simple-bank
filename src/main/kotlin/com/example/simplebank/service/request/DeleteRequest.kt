package com.example.simplebank.service.request

import javax.validation.constraints.Positive

data class DeleteRequest(
    @field:Positive
    val accountId: Int,

    @field:Positive
    val customerId: Int,
)