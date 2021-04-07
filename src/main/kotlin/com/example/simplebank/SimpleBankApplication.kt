package com.example.simplebank

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class SimpleBankApplication

fun main(args: Array<String>) {
    runApplication<SimpleBankApplication>(*args)
}