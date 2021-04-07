package com.example.simplebank.exception.exceptions

class NoCustomersException(
    override val message: String?,
) : Exception(message)