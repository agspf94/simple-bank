package com.example.simplebank.integration

import com.example.simplebank.entity.Customer
import com.example.simplebank.service.request.DeleteRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import javax.validation.Valid

//@FeignClient(url = "https://webhook.site/d526f75d-99f3-488f-8f47-38355e6f0ffe", name = "customer-api-client")
@FeignClient(url = "\${feign.url}", name = "customer-api-client")
interface CustomerApiClient {
    @GetMapping
    fun findAll(): List<Customer>

    @GetMapping
    fun findByCustomerId(@RequestParam("customerId") customerId: Int): Customer

    @PostMapping
    fun validateDelete(@Valid @RequestBody deleteObject: DeleteRequest): ResponseEntity<Unit>
}