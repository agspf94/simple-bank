package com.example.simplebank

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SimpleBankApplication

fun main(args: Array<String>) {
    runApplication<SimpleBankApplication>(*args)
}
