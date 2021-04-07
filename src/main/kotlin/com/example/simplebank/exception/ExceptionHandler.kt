package com.example.simplebank.exception

import com.example.simplebank.exception.exceptions.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(
        NoneAccountException::class,
        NoCustomersException::class,
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    fun handleNoContent(e: Exception): Error = Error(e)

    @ExceptionHandler(
        CustomerAlreadyExistsException::class,
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleBadRequest(e: Exception): Error = Error(e)

    @ExceptionHandler(
        CustomerIdNotFoundException::class,
    )
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun handleCustomerIdNotFoundException(e: Exception): Error = Error(e)

    @ExceptionHandler(
        DeletionFailedException::class,
    )
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    fun handleDeletionFailedException(e: Exception): Error = Error(e)
}