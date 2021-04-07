package com.example.simplebank.component

import com.example.simplebank.SimpleBankApplication
import com.example.simplebank.mock.CustomerApiClientMockStarter
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest(classes = [SimpleBankApplication::class], webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(value = [CustomerApiClientMockStarter::class])
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SqlGroup(
    Sql("classpath:scripts/data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
    Sql("classpath:scripts/reset.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
)
abstract class AbstractComponentTest {
    protected lateinit var wireMockServer: WireMockServer

    @BeforeAll
    fun startWireMock() {
        wireMockServer = WireMockServer(WireMockConfiguration
            .wireMockConfig()
            .port(8080)
        )
    }

    @AfterAll
    fun tearDownWireMock() {
        wireMockServer.stop()
    }

    @BeforeEach
    fun setup() {
        if (!wireMockServer.isRunning) {
            wireMockServer.start()
        }
    }
}