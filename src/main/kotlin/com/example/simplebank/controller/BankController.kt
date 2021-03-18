package com.example.simplebank.controller

import com.example.simplebank.entity.Bank
import com.example.simplebank.service.BankService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/banks")
class BankController(
    @Autowired
    private val service: BankService
) {
    @GetMapping
    fun getBanks(): ResponseEntity<List<Bank>> = service.getBanks()

    @GetMapping("/{accountNumber}")
    fun getBank(@PathVariable("accountNumber") accountNumber: Int): ResponseEntity<Optional<Bank>> =
        service.getBank(accountNumber)

    @PostMapping
    fun addBank(@RequestBody bank: Bank): ResponseEntity<Bank> =
        service.addBank(bank)

    @PutMapping("/{accountNumber}")
    fun updateBank(@PathVariable("accountNumber") accountNumber: Int, @RequestBody bank: Bank): ResponseEntity<Bank> =
        service.updateBank(accountNumber, bank)

    @DeleteMapping("/{accountNumber}")
    fun deleteBank(@PathVariable("accountNumber") accountNumber: Int): ResponseEntity<Unit> =
        service.deleteBank(accountNumber)

}