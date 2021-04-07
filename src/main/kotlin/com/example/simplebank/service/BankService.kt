package com.example.simplebank.service

import com.example.simplebank.service.request.DeleteRequest
import com.example.simplebank.controller.request.BankRequest
import com.example.simplebank.entity.Bank
import com.example.simplebank.controller.response.BankResponse
import com.example.simplebank.exception.exceptions.*
import com.example.simplebank.repository.BankRepository
import com.example.simplebank.integration.CustomerApiClient
import feign.FeignException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class BankService(
    @Autowired
    private val bankRepository: BankRepository,

    @Autowired
    private val customerApiClient: CustomerApiClient,
) {
    fun getBanks(): List<Bank> = bankRepository.findAll().ifEmpty { throw NoneAccountException("There are no accounts yet") }

    fun getBank(customerId: Int): BankResponse {
        return try {
            val customer = customerApiClient.findByCustomerId(customerId)
            val bank = bankRepository
                .findByCustomerId(customerId)
                .orElseThrow { CustomerIdNotFoundException("The given customer (Customer ID: $customerId) was not found on Bank API") }
            BankResponse(bank, customer)
        } catch (e: FeignException.NotFound) {
            throw CustomerIdNotFoundException("The given customer (Customer ID: $customerId) was not found on Customer API")
        }
    }

    fun addBank(bankRequest: BankRequest): BankResponse {
        try {
            val customer = customerApiClient.findByCustomerId(bankRequest.customerId)
            bankRepository
                .findByCustomerId(customer.customerId)
                .ifPresent { throw CustomerAlreadyExistsException("The given customer (Customer ID: ${customer.customerId}) already exists on Bank API") }
            return BankResponse(bankRepository.save(Bank(bankRequest)), customer)
        } catch (e: FeignException.NotFound) {
            throw CustomerIdNotFoundException("The given customer (Customer ID: ${bankRequest.customerId}) was not found on Customer API")
        }
    }

    fun updateBank(bankRequest: BankRequest): BankResponse {
        try {
            val customer = customerApiClient.findByCustomerId(bankRequest.customerId)
            val bankToBeUpdated = bankRepository
                .findByCustomerId(customer.customerId)
                .orElseThrow { CustomerIdNotFoundException("The given customer (Customer ID: ${customer.customerId}) was not found on Bank API") }
                bankToBeUpdated.accountNumber = bankRequest.accountNumber
                bankToBeUpdated.balance = bankRequest.balance
                bankRepository.save(bankToBeUpdated)
                return BankResponse(bankToBeUpdated, customer)
        } catch (e: FeignException.NotFound) {
            throw CustomerIdNotFoundException("The given customer (Customer ID: ${bankRequest.customerId}) was not found on Customer API")
        }
    }

    fun deleteBank(customerId: Int) {
        try {
            val customer = customerApiClient.findByCustomerId(customerId)
            val bankToDelete = bankRepository
                .findByCustomerId(customer.customerId)
                .orElseThrow { CustomerIdNotFoundException("The given customer (Customer ID: ${customer.customerId}) was not found on Bank API") }
            if (isDeletionValid(bankToDelete)) {
                bankRepository.delete(bankToDelete)
            }
        } catch (e: FeignException.NotFound) {
            throw CustomerIdNotFoundException("The given customer (Customer ID: $customerId) was not found on Customer API")
        } catch (e: FeignException.UnprocessableEntity) {
            throw DeletionFailedException("The deletion failed!")
        }
    }

    private fun isDeletionValid(bankToDelete: Bank) =
        customerApiClient.validateDelete(
            DeleteRequest(
                bankToDelete.accountNumber,
                bankToDelete.customerId
            )
        ).statusCode == HttpStatus.OK
}