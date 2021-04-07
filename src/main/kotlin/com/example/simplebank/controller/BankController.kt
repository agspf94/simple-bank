package com.example.simplebank.controller

import com.example.simplebank.controller.request.BankRequest
import com.example.simplebank.controller.response.BankResponse
import com.example.simplebank.entity.Bank
import com.example.simplebank.service.BankService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/banks")
class BankController(
    @Autowired
    private val service: BankService
) {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getBanks(): List<Bank> = service.getBanks()

    @GetMapping("/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    fun getBank(@PathVariable("customerId") customerId: Int): BankResponse = service.getBank(customerId)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBank(@Valid @RequestBody bankRequest: BankRequest): BankResponse = service.addBank(bankRequest)

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    fun updateBank(@Valid @RequestBody bankRequest: BankRequest): BankResponse = service.updateBank(bankRequest)

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    fun deleteBank(@PathVariable("customerId") customerId: Int): Unit = service.deleteBank(customerId)
}