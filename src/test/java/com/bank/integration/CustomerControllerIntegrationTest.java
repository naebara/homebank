package com.bank.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerIntegrationTest {

    @Autowired
    private WebTestClient client;

    @Test
    @DisplayName("Get all customers")
    public void getAllClientsTest_ShouldReturnAllClients() {
        client
                .get()
                .uri("/v1/customers")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("All customers");

    }

    @Test
    @DisplayName("Get customer by id")
    public void getCustomerById_shouldReturnCustomerById() {
        client.get()
                .uri("/v1/customers/3")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("User with id 3");
    }

    @Test
    @DisplayName("Delete customer by id")
    public void deleteCustomerById() {
        client.delete()
                .uri("/v1/customers/3")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Deleted user with id 3");
    }


    @Test
    @DisplayName("Update customer")
    public void updateCustomer() {
        client.put()
                .uri("/v1/customers")
                .bodyValue("user")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .consumeWith(exchangeResult -> {
                    String response = exchangeResult.getResponseBody();
                    assertEquals("Updated user : user", response);
                });
    }

}
