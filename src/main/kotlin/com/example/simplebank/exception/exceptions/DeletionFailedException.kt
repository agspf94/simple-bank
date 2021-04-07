package com.example.simplebank.exception.exceptions

class DeletionFailedException(
    override val message: String?,
) : Exception(message)