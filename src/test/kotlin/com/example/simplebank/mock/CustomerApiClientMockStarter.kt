package com.example.simplebank.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import org.springframework.boot.test.context.TestConfiguration

@TestConfiguration
class CustomerApiClientMockStarter {
    fun stubForGetBankSuccessfully(wireMockServer: WireMockServer) {
        wireMockServer.stubFor(
            WireMock
                .get(WireMock.urlPathEqualTo("/customer/customer"))
                .withQueryParam("customer", equalTo("1"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("content-type", "application/json")
                        .withBody("{\n" +
                                "    \"customerId\": 1,\n" +
                                "    \"name\": \"Anderson\",\n" +
                                "    \"email\": \"anderson@pagseguro.com.br\"\n" +
                                "}")
                )
        )
    }
}