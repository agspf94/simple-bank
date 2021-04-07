package com.example.simplebank.exception.exceptions

class CustomerAlreadyExistsException(
    override val message: String?,
) : Exception(message)