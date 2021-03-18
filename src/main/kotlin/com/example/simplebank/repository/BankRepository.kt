package com.example.simplebank.repository

import com.example.simplebank.entity.Bank
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.ResponseEntity
import java.util.*

interface BankRepository : JpaRepository<Bank, Int> {
    fun findByAccountNumber(accountNumber: Int): Optional<Bank>
}