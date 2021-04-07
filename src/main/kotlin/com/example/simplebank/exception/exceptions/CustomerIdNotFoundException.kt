package com.example.simplebank.exception.exceptions

class CustomerIdNotFoundException(
    override val message: String?,
) : Exception(message)