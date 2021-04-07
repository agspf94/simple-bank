package com.example.simplebank.repository

import com.example.simplebank.entity.Bank
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BankRepository : JpaRepository<Bank, Int> {
    fun findByCustomerId(customerId: Int): Optional<Bank>
}