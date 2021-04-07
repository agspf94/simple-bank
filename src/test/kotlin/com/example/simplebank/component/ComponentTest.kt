package com.example.simplebank.component

import com.example.simplebank.controller.request.BankRequest
import com.example.simplebank.entity.Bank
import com.example.simplebank.mock.CustomerApiClientMockStarter
import com.example.simplebank.repository.BankRepository
import com.example.simplebank.service.request.DeleteRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ComponentTest : AbstractComponentTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var bankRepository: BankRepository

    @Autowired
    private lateinit var customerApiClientMockStater: CustomerApiClientMockStarter

    @BeforeEach
    override fun setupMocks() {
        bankRepository.deleteAll()
        val banks = mutableListOf<Bank>()
        banks.add(Bank(1, 1, 100.0, 1))
        banks.add(Bank(2, 2, 100.0, 2))
        banks.add(Bank(3, 3, 100.0, 3))
        banks.add(Bank(4, 4, 100.0, 4))
        bankRepository.saveAll(banks)
    }

    @Nested
    @DisplayName("Getting all banks")
    inner class GetBanks {
        @Test
        fun `should get banks successfully`() {
            mockMvc.perform(get("/api/banks"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.[0].accountNumber").value(1))
                .andExpect(jsonPath("$.[0].balance").value(100.0))
                .andExpect(jsonPath("$.[0].customerId").value(1))
                .andExpect(jsonPath("$.[1].accountNumber").value(2))
                .andExpect(jsonPath("$.[1].balance").value(100.0))
                .andExpect(jsonPath("$.[1].customerId").value(2))
                .andExpect(jsonPath("$.[2].accountNumber").value(3))
                .andExpect(jsonPath("$.[2].balance").value(100.0))
                .andExpect(jsonPath("$.[2].customerId").value(3))
                .andExpect(jsonPath("$.[3].accountNumber").value(4))
                .andExpect(jsonPath("$.[3].balance").value(100.0))
                .andExpect(jsonPath("$.[3].customerId").value(4))
                .andExpect(jsonPath("$.[4]").doesNotExist())
        }

        @Test
        fun `should get empty banks list`() {
            bankRepository.deleteAll()
            mockMvc.perform(get("/api/banks"))
                .andExpect(status().isNoContent)
        }
    }

    @Nested
    @DisplayName("Getting a bank")
    inner class GetBank {
        @Test
        fun `should get a bank successfully`() {
            customerApiClientMockStater.stubForGet(
                wireMockServer,
                "1",
                200,
                "{\n" +
                        "    \"customerId\": 1,\n" +
                        "    \"name\": \"Anderson\",\n" +
                        "    \"email\": \"anderson@pagseguro.com.br\"\n" +
                        "}"
            )
            mockMvc.perform(get("/api/banks/1"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.accountNumber").value(1))
                .andExpect(jsonPath("$.balance").value(100.0))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.name").value("Anderson"))
                .andExpect(jsonPath("$.email").value("anderson@pagseguro.com.br"))
        }

        @Test
        fun `should not get a bank because customer was not found on Bank API`() {
            customerApiClientMockStater.stubForGet(
                wireMockServer,
                "5",
                200,
                "{\n" +
                        "    \"customerId\": 5,\n" +
                        "    \"name\": \"Fantin\",\n" +
                        "    \"email\": \"fantin@pagseguro.com.br\"\n" +
                        "}"
            )
            val customerId = 5
            mockMvc.perform(get("/api/banks/{customerId}", customerId))
                .andExpect(status().isNotFound)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cause.message").value("The given customer (Customer ID: $customerId) was not found on Bank API"))
        }

        @Test
        fun `should not get a bank because customer was not found on Customer API`() {
            customerApiClientMockStater.stubForGet(
                wireMockServer,
                "5",
                404,
                "{\n" +
                        "    \"customerId\": 5,\n" +
                        "    \"name\": \"Fantin\",\n" +
                        "    \"email\": \"fantin@pagseguro.com.br\"\n" +
                        "}"
            )
            val customerId = 5
            mockMvc.perform(get("/api/banks/{customerId}", customerId))
                .andExpect(status().isNotFound)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cause.message").value("The given customer (Customer ID: $customerId) was not found on Customer API"))
        }
    }

    @Nested
    @DisplayName("Adding a new bank")
    inner class AddBank {
        @Test
        fun `should add a new bank successfully`() {
            customerApiClientMockStater.stubForGet(
                wireMockServer,
                "5",
                200,
                "{\n" +
                        "    \"customerId\": 5,\n" +
                        "    \"name\": \"Fantin\",\n" +
                        "    \"email\": \"fantin@pagseguro.com.br\"\n" +
                        "}"
            )
            val request = BankRequest(5, 100.0, 5)
            mockMvc.perform(
                        post("/api/banks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.accountNumber").value(5))
                .andExpect(jsonPath("$.balance").value(100.0))
                .andExpect(jsonPath("$.customerId").value(5))
                .andExpect(jsonPath("$.name").value("Fantin"))
                .andExpect(jsonPath("$.email").value("fantin@pagseguro.com.br"))
        }

        @Test
        fun `should not add a new bank because customer doesnt exist on Customer API`() {
            customerApiClientMockStater.stubForGet(
                wireMockServer,
                "5",
                404,
                "{\n" +
                        "    \"customerId\": 5,\n" +
                        "    \"name\": \"Fantin\",\n" +
                        "    \"email\": \"fantin@pagseguro.com.br\"\n" +
                        "}"
            )
            val request = BankRequest(5, 100.0, 5)
            mockMvc.perform(
                post("/api/banks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should not add a new bank because this bank already exists`() {
            customerApiClientMockStater.stubForGet(
                wireMockServer,
                "1",
                200,
                "{\n" +
                        "    \"customerId\": 1,\n" +
                        "    \"name\": \"Anderson\",\n" +
                        "    \"email\": \"anderson@pagseguro.com.br\"\n" +
                        "}"
            )
            val request = BankRequest(1, 100.0, 1)
            mockMvc.perform(
                post("/api/banks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest)
        }
    }

    @Nested
    @DisplayName("Updating a bank")
    inner class UpdateBank {
        @Test
        fun `should update a bank successfully`() {
            customerApiClientMockStater.stubForGet(
                wireMockServer,
                "1",
                200,
                "{\n" +
                        "    \"customerId\": 1,\n" +
                        "    \"name\": \"Anderson\",\n" +
                        "    \"email\": \"anderson@pagseguro.com.br\"\n" +
                        "}"
            )
            val request = BankRequest(1, 110.0, 1)
            mockMvc.perform(
                put("/api/banks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.accountNumber").value(1))
                .andExpect(jsonPath("$.balance").value(110.0))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.name").value("Anderson"))
                .andExpect(jsonPath("$.email").value("anderson@pagseguro.com.br"))
        }

        @Test
        fun `should not update a bank because the customer was not found on Customer API`() {
            customerApiClientMockStater.stubForGet(
                wireMockServer,
                "5",
                404,
                "{\n" +
                        "    \"customerId\": 5,\n" +
                        "    \"name\": \"Fantin\",\n" +
                        "    \"email\": \"fantin@pagseguro.com.br\"\n" +
                        "}"
            )
            val request = BankRequest(5, 110.0, 5)
            mockMvc.perform(
                put("/api/banks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should not update a bank because the customer was not found on Bank API`() {
            customerApiClientMockStater.stubForGet(
                wireMockServer,
                "5",
                200,
                "{\n" +
                        "    \"customerId\": 5,\n" +
                        "    \"name\": \"Fantin\",\n" +
                        "    \"email\": \"fantin@pagseguro.com.br\"\n" +
                        "}"
            )
            val request = BankRequest(5, 110.0, 5)
            mockMvc.perform(
                put("/api/banks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound)
        }
    }

    @Nested
    @DisplayName("Deleting a bank")
    inner class DeleteBank {
        @Test
        fun `should delete a bank successfully`() {
            customerApiClientMockStater.stubForGet(
                wireMockServer,
                "1",
                200,
                "{\n" +
                        "    \"customerId\": 1,\n" +
                        "    \"name\": \"Anderson\",\n" +
                        "    \"email\": \"anderson@pagseguro.com.br\"\n" +
                        "}"
            )
            customerApiClientMockStater.stubForValidationDelete(
                wireMockServer,
                200,
                DeleteRequest(1, 1)
            )
            mockMvc.perform(delete("/api/banks/{customerId}", 1))
                .andExpect(status().isOk)
        }

        @Test
        fun `should not delete a bank because it was not found on Customer API`() {
            customerApiClientMockStater.stubForGet(
                wireMockServer,
                "5",
                404,
                "{\n" +
                        "    \"customerId\": 5,\n" +
                        "    \"name\": \"Fantin\",\n" +
                        "    \"email\": \"fantin@pagseguro.com.br\"\n" +
                        "}"
            )
            mockMvc.perform(delete("/api/banks/{customerId}", 5))
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should not delete a bank because it was not found on Bank API`() {
            customerApiClientMockStater.stubForGet(
                wireMockServer,
                "5",
                200,
                "{\n" +
                        "    \"customerId\": 5,\n" +
                        "    \"name\": \"Fantin\",\n" +
                        "    \"email\": \"fantin@pagseguro.com.br\"\n" +
                        "}"
            )
            mockMvc.perform(delete("/api/banks/{customerId}", 5))
                .andExpect(status().isNotFound)
        }

        @Test
        fun `should not delete a bank because the deletion failed`() {
            customerApiClientMockStater.stubForGet(
                wireMockServer,
                "1",
                200,
                "{\n" +
                        "    \"customerId\": 1,\n" +
                        "    \"name\": \"Anderson\",\n" +
                        "    \"email\": \"anderson@pagseguro.com.br\"\n" +
                        "}"
            )
            customerApiClientMockStater.stubForValidationDelete(
                wireMockServer,
                422,
                DeleteRequest(1, 1)
            )
            mockMvc.perform(delete("/api/banks/{customerId}", 1))
                .andExpect(status().isUnprocessableEntity)
        }
    }
}