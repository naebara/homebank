package com.bank.integration;

import com.bank.controller.AccountController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureWebTestClient
@WebFluxTest(controllers = AccountController.class)
public class AccountControllerIntegrationTest {

    @Autowired
    private WebTestClient client;

    @Test
    @DisplayName("Get all accounts")
    public void getAllAccountsTest_ShouldReturnAllAccounts() {
        client
                .get()
                .uri("/v1/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    String response = stringEntityExchangeResult.getResponseBody();
                    assertEquals("All accounts", response);
                });

    }

    @Test
    @DisplayName("Get account by id")
    public void getAccountById_shouldReturnAccountById() {
        client.get()
                .uri("/v1/accounts/3")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Account with id 3");
    }

    @Test
    @DisplayName("Delete account by id")
    public void deleteAccountById() {
        client.delete()
                .uri("/v1/accounts/3")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Deleted account with id 3");
    }


    @Test
    @DisplayName("Update account")
    public void updateAccount() {
        client.put()
                .uri("/v1/accounts")
                .bodyValue("myAccount")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .consumeWith(exchangeResult -> {
                    String response = exchangeResult.getResponseBody();
                    assertEquals("Updated account : myAccount", response);
                });
    }

}
