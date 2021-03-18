package com.example.simplebank.service

import com.example.simplebank.entity.Bank
import com.example.simplebank.repository.BankRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class BankService(
    @Autowired
    private val repository: BankRepository
) {
    fun getBanks(): ResponseEntity<List<Bank>> = ResponseEntity.ok(repository.findAll())

    fun getBank(accountNumber: Int): ResponseEntity<Optional<Bank>> {
        val optional: Optional<Bank> = repository.findByAccountNumber(accountNumber)
        return if (optional.isPresent) {
            ResponseEntity.ok(optional)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    fun addBank(bank: Bank): ResponseEntity<Bank> {
        val optional: Optional<Bank> = repository.findByAccountNumber(bank.accountNumber)
        return if (!optional.isPresent) {
            ResponseEntity.ok(repository.save(bank))
        } else {
            ResponseEntity.badRequest().build()
        }
    }

    fun updateBank(accountNumber: Int, bank: Bank): ResponseEntity<Bank> {
        val optional: Optional<Bank> = repository.findByAccountNumber(accountNumber)
        return if (optional.isPresent) {
            val updated: Bank = optional.get()
            updated.accountNumber = bank.accountNumber
            updated.owner = bank.owner
            updated.balance = bank.balance
            ResponseEntity.ok(repository.save(updated))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    fun deleteBank(accountNumber: Int): ResponseEntity<Unit> {
        val optional: Optional<Bank> = repository.findByAccountNumber(accountNumber)
        return if (optional.isPresent) {
            repository.delete(optional.get())
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}