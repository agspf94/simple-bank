package com.example.simplebank.integration

import com.example.simplebank.entity.Customer
import com.example.simplebank.service.request.DeleteRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@FeignClient(url = "\${feign.client.config.customerApiClient.url}", name = "customer-api-client")
interface CustomerApiClient {
    @GetMapping
    fun findAll(): List<Customer>

    @GetMapping("/{customerId}")
    fun findByCustomerId(@PathVariable("customerId") customerId: Int): Customer

    @PostMapping
    fun validateDelete(@Valid @RequestBody deleteObject: DeleteRequest): ResponseEntity<Unit>
}