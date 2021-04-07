package com.example.simplebank.service

import com.example.simplebank.service.request.DeleteRequest
import com.example.simplebank.controller.request.BankRequest
import com.example.simplebank.entity.Bank
import com.example.simplebank.entity.Customer
import com.example.simplebank.exception.exceptions.CustomerAlreadyExistsException
import com.example.simplebank.exception.exceptions.CustomerIdNotFoundException
import com.example.simplebank.exception.exceptions.DeletionFailedException
import com.example.simplebank.exception.exceptions.NoneAccountException
import com.example.simplebank.integration.CustomerApiClient
import com.example.simplebank.repository.BankRepository
import feign.FeignException
import feign.Request
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*
import feign.RequestTemplate
import org.mockito.*
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import java.util.HashMap

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class BankServiceTest {
    @InjectMocks
    private lateinit var bankService: BankService

    @Mock
    private lateinit var bankRepository: BankRepository

    @Mock
    private lateinit var customerApiClient: CustomerApiClient

    @Captor
    private lateinit var bankToBeUpdatedCaptor: ArgumentCaptor<Bank>

    @Nested
    @DisplayName("Getting all banks")
    inner class GetBanks {
        @Test
        fun `should get banks successfully`() {
            val banksList = mutableListOf<Bank>()
            banksList.add(Bank(1, 1, 100.0, 1))
            banksList.add(Bank(2, 2, 100.0, 2))
            banksList.add(Bank(3, 3, 100.0, 3))
            banksList.add(Bank(4, 4, 100.0, 4))
            `when`(bankRepository.findAll()).thenReturn(banksList)

            val response = bankService.getBanks()

            assertEquals(banksList.size, response.size)
            assertEquals(banksList, response)

            verify(bankRepository, times(1)).findAll()
        }

        @Test
        fun `should throw NoneAccountException`() {
            `when`(bankRepository.findAll()).thenReturn(listOf<Bank>())
            assertThrows(NoneAccountException::class.java) { bankService.getBanks() }
            verify(bankRepository, times(1)).findAll()
        }
    }
    
    @Nested
    @DisplayName("Getting a bank")
    inner class GetBank {
        @Test
        fun `should get bank successfully`() {
            val customer = Customer(1, "Anderson", "anderson@pagseguro.com.br")
            val bank = Bank(1, 1, 100.0, 1)
            `when`(customerApiClient.findByCustomerId(customer.customerId)).thenReturn(customer)
            `when`(bankRepository.findByCustomerId(customer.customerId)).thenReturn(Optional.of(bank))

            val response = bankService.getBank(customer.customerId)
            assertEquals(bank.id, response.id)
            assertEquals(bank.accountNumber, response.accountNumber)
            assertEquals(bank.balance, response.balance)
            assertEquals(bank.customerId, response.customerId)
            assertEquals(customer.name, response.name)
            assertEquals(customer.email, response.email)

            verify(customerApiClient, times(1)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(1)).findByCustomerId(customer.customerId)
        }

        @Test
        fun `should throw CustomerIdNotFoundException from Bank API`() {
            val customer = Customer(1, "Anderson", "anderson@pagseguro.com.br")
            `when`(customerApiClient.findByCustomerId(customer.customerId)).thenReturn(customer)
            `when`(bankRepository.findByCustomerId(customer.customerId)).thenReturn(Optional.empty())

            assertThrows(CustomerIdNotFoundException::class.java) { bankService.getBank(customer.customerId) }
            verify(customerApiClient, times(1)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(1)).findByCustomerId(customer.customerId)
        }

        @Test
        fun `should throw CustomerIdNotFoundException from Customer API`() {
            val customer = Customer(1, "Anderson", "anderson@pagseguro.com.br")
            `when`(customerApiClient.findByCustomerId(customer.customerId))
                .thenThrow(FeignException.NotFound(
                    "",
                    Request.create(
                        Request.HttpMethod.GET,
                        "url",
                        HashMap(),
                        null,
                        RequestTemplate()
                    ),
                    null))

            assertThrows(CustomerIdNotFoundException::class.java) { bankService.getBank(customer.customerId) }
            verify(customerApiClient, times(1)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(0)).findByCustomerId(customer.customerId)
        }
    }
    
    @Nested
    @DisplayName("Adding a new bank")
    inner class AddBank {
        @Test
        fun `should add bank successfully`() {
            val customer = Customer(5, "Fantin", "fantin@pagseguro.com.br")
            val request = BankRequest(5, 100.0, 5)
            val bank = Bank(request)
            `when`(customerApiClient.findByCustomerId(customer.customerId)).thenReturn(customer)
            `when`(bankRepository.findByCustomerId(customer.customerId)).thenReturn(Optional.empty())
            `when`(bankRepository.save(bank)).thenReturn(bank)

            val response = bankService.addBank(request)
            assertEquals(bank.id, response.id)
            assertEquals(request.accountNumber, response.accountNumber)
            assertEquals(request.balance, response.balance)
            assertEquals(request.customerId, response.customerId)
            assertEquals(customer.name, response.name)
            assertEquals(customer.email, response.email)

            verify(customerApiClient, times(1)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(1)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(1)).save(bank)
        }
        
        @Test
        fun `should throw CustomerAlreadyExistsException`() {
            val customer = Customer(5, "Fantin", "fantin@pagseguro.com.br")
            val request = BankRequest(5, 100.0, 5)
            val bank = Bank(request)
            `when`(customerApiClient.findByCustomerId(customer.customerId)).thenReturn(customer)
            `when`(bankRepository.findByCustomerId(customer.customerId)).thenReturn(Optional.of(bank))

            assertThrows(CustomerAlreadyExistsException::class.java) { bankService.addBank(request) }

            verify(customerApiClient, times(1)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(1)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(0)).save(bank)
        }
        
        @Test
        fun `should throw CustomerIdNotFoundException`() {
            val customer = Customer(5, "Fantin", "fantin@pagseguro.com.br")
            val request = BankRequest(5, 100.0, 5)
            val bank = Bank(request)
            `when`(customerApiClient.findByCustomerId(customer.customerId))
                .thenThrow(FeignException.NotFound(
                    "",
                    Request.create(
                        Request.HttpMethod.GET,
                        "url",
                        HashMap(),
                        null,
                        RequestTemplate()
                    ),
                    null))

            assertThrows(CustomerIdNotFoundException::class.java) { bankService.addBank(request) }

            verify(customerApiClient, times(1)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(0)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(0)).save(bank)
        }
    }
    
    @Nested
    @DisplayName("Updating an existing bank")
    inner class UpdateBank {
        @Test
        fun `should update bank successfully`() {
            val customer = Customer(5, "Fantin", "fantin@pagseguro.com.br")
            val request = BankRequest(5, 110.0, 5)
            val bank = Bank(request)
            `when`(customerApiClient.findByCustomerId(customer.customerId)).thenReturn(customer)
            `when`(bankRepository.findByCustomerId(customer.customerId)).thenReturn(Optional.of(bank))

            val response = bankService.updateBank(request)

            verify(bankRepository).save(bankToBeUpdatedCaptor.capture())
            val bankToBeUpdated = bankToBeUpdatedCaptor.value
            assertEquals(bank.id, bankToBeUpdated.id)
            assertEquals(request.accountNumber, bankToBeUpdated.accountNumber)
            assertEquals(request.balance, bankToBeUpdated.balance)
            assertEquals(request.customerId, bankToBeUpdated.customerId)

            assertEquals(bank.id, response.id)
            assertEquals(request.accountNumber, response.accountNumber)
            assertEquals(request.balance, response.balance)
            assertEquals(request.customerId, response.customerId)
            assertEquals(customer.name, response.name)
            assertEquals(customer.email, response.email)

            verify(customerApiClient, times(1)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(1)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(1)).save(bank)
        }
        
        @Test
        fun `should throw CustomerIdNotFoundException from Bank API`() {
            val customer = Customer(5, "Fantin", "fantin@pagseguro.com.br")
            val request = BankRequest(5, 110.0, 5)
            val bank = Bank(request)
            `when`(customerApiClient.findByCustomerId(customer.customerId)).thenReturn(customer)
            `when`(bankRepository.findByCustomerId(customer.customerId)).thenReturn(Optional.empty())

            assertThrows(CustomerIdNotFoundException::class.java) { bankService.updateBank(request) }

            verify(customerApiClient, times(1)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(1)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(0)).save(bank)
        }
        
        @Test
        fun `should throw CustomerIdNotFoundException from Customer API`() {
            val customer = Customer(5, "Fantin", "fantin@pagseguro.com.br")
            val request = BankRequest(5, 110.0, 5)
            val bank = Bank(request)
            `when`(customerApiClient.findByCustomerId(customer.customerId))
                .thenThrow(FeignException.NotFound(
                    "",
                    Request.create(
                        Request.HttpMethod.GET,
                        "url",
                        HashMap(),
                        null,
                        RequestTemplate()
                    ),
                    null))

            assertThrows(CustomerIdNotFoundException::class.java) { bankService.updateBank(request) }

            verify(customerApiClient, times(1)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(0)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(0)).save(bank)
        }
    }
    
    @Nested
    @DisplayName("Deleting an existing bank")
    inner class DeleteBank {
        @Test
        fun `should delete bank successfully`() {
            val customer = Customer(5, "Fantin", "fantin@pagseguro.com.br")
            val bank = Bank(5,5, 100.0, 5)
            `when`(customerApiClient.findByCustomerId(customer.customerId)).thenReturn(customer)
            `when`(bankRepository.findByCustomerId(customer.customerId)).thenReturn(Optional.of(bank))
            `when`(customerApiClient.validateDelete(DeleteRequest(bank.accountNumber, customer.customerId)))
                .thenReturn(ResponseEntity<Unit>(Unit, HttpStatus.OK))

            bankService.deleteBank(customer.customerId)

            verify(customerApiClient, times(1)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(1)).findByCustomerId(customer.customerId)
            verify(customerApiClient, times(1)).validateDelete(DeleteRequest(bank.accountNumber, customer.customerId))
            verify(bankRepository, times(1)).delete(bank)
        }
        
        @Test
        fun `should throw CustomerIdNotFoundException from Bank API`() {
            val customer = Customer(5, "Fantin", "fantin@pagseguro.com.br")
            val bank = Bank(5,5, 100.0, 5)
            `when`(customerApiClient.findByCustomerId(customer.customerId)).thenReturn(customer)
            `when`(bankRepository.findByCustomerId(customer.customerId)).thenReturn(Optional.empty())

            assertThrows(CustomerIdNotFoundException::class.java) { bankService.deleteBank(customer.customerId) }

            verify(customerApiClient, times(1)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(1)).findByCustomerId(customer.customerId)
            verify(customerApiClient, times(0)).validateDelete(DeleteRequest(bank.accountNumber, customer.customerId))
            verify(bankRepository, times(0)).delete(bank)
        }
        
        @Test
        fun `should throw CustomerIdNotFoundException from Customer API`() {
            val customer = Customer(5, "Fantin", "fantin@pagseguro.com.br")
            val bank = Bank(5,5, 100.0, 5)
            `when`(customerApiClient.findByCustomerId(customer.customerId))
                .thenThrow(FeignException.NotFound(
                    "",
                    Request.create(
                        Request.HttpMethod.GET,
                        "url",
                        HashMap(),
                        null,
                        RequestTemplate()
                    ),
                    null))

            assertThrows(CustomerIdNotFoundException::class.java) { bankService.deleteBank(customer.customerId) }

            verify(customerApiClient, times(1)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(0)).findByCustomerId(customer.customerId)
            verify(customerApiClient, times(0)).validateDelete(DeleteRequest(bank.accountNumber, customer.customerId))
            verify(bankRepository, times(0)).delete(bank)
        }
        
        @Test
        fun `should throw DeletionFailedException`() {
            val customer = Customer(5, "Fantin", "fantin@pagseguro.com.br")
            val bank = Bank(5,5, 100.0, 5)
            `when`(customerApiClient.findByCustomerId(customer.customerId)).thenReturn(customer)
            `when`(bankRepository.findByCustomerId(customer.customerId)).thenReturn(Optional.of(bank))
            `when`(customerApiClient.validateDelete(DeleteRequest(bank.accountNumber, customer.customerId)))
                .thenThrow(FeignException.UnprocessableEntity(
                    "",
                    Request.create(
                        Request.HttpMethod.GET,
                        "url",
                        HashMap(),
                        null,
                        RequestTemplate()
                    ),
                    null))

            assertThrows(DeletionFailedException::class.java) { bankService.deleteBank(customer.customerId) }

            verify(customerApiClient, times(1)).findByCustomerId(customer.customerId)
            verify(bankRepository, times(1)).findByCustomerId(customer.customerId)
            verify(customerApiClient, times(1)).validateDelete(DeleteRequest(bank.accountNumber, customer.customerId))
            verify(bankRepository, times(0)).delete(bank)
        }
    }
}