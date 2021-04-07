package com.example.simplebank.exception.exceptions

class NoneAccountException(
    override val message: String?,
) : Exception(message)