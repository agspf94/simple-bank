package com.example.simplebank.mock

import com.example.simplebank.service.request.DeleteRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.springframework.boot.test.context.TestConfiguration

@TestConfiguration
class CustomerApiClientMockStarter {
    fun stubForGet(wireMockServer: WireMockServer, url: String, status: Int, json: String) {
        wireMockServer.stubFor(
            WireMock
                .get(WireMock.urlPathEqualTo("/$url"))
                .willReturn(
                    aResponse()
                        .withStatus(status)
                        .withHeader("content-type", "application/json")
                        .withBody(json)
                )
        )
    }


    fun stubForValidationDelete(wireMockServer: WireMockServer, status: Int, deleteRequest: DeleteRequest) {
        wireMockServer.stubFor(
            WireMock
                .post(WireMock.urlPathEqualTo("/"))
                .withRequestBody(equalToJson(ObjectMapper().writeValueAsString(deleteRequest)))
                .willReturn(
                    aResponse()
                        .withStatus(status)
                )
        )
    }
}