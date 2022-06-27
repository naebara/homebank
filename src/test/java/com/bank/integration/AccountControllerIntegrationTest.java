package com.bank.integration;

import com.bank.controller.AccountController;
import com.bank.model.dto.AccountDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureWebTestClient
@WebFluxTest(controllers = AccountController.class)
public class AccountControllerIntegrationTest {

    @Autowired
    private WebTestClient client;

    List<AccountDto> accounts = Arrays.asList(
            AccountDto.builder().id(1).customerId(1).number("asdfkljasdsdfj").amount(new BigDecimal(400)).issuedAt(LocalDate.now()).build(),
            AccountDto.builder().id(2).customerId(2).number("asdagfkljasdsdfj").amount(new BigDecimal(100)).issuedAt(LocalDate.now()).build(),
            AccountDto.builder().id(3).customerId(3).number("asdfkafdhgljasdsdfj").amount(new BigDecimal(300)).issuedAt(LocalDate.now()).build()
    );

    @Test
    @DisplayName("Get all accounts")
    public void getAllAccountsTest_ShouldReturnAllAccounts() {
        client
                .get()
                .uri("/v1/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBodyList(AccountDto.class)
                .consumeWith(exchangeResult -> {
                    List<AccountDto> response = exchangeResult.getResponseBody();
                    assertNotNull(response);
                    assertEquals(3, response.size());
                    assertEquals(accounts, response);
                });

    }

    @Test
    @DisplayName("Get account by id")
    public void getAccountById_shouldReturnAccountById() {
        client.get()
                .uri("/v1/accounts/3")
//                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AccountDto.class)
                .isEqualTo(accounts.get(0));
    }

    @Test
    @DisplayName("Delete account by id")
    public void deleteAccountById() {
        client.delete()
                .uri("/v1/accounts/3")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AccountDto.class)
                .isEqualTo(accounts.get(0));
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
                .expectBody(AccountDto.class)
                .consumeWith(exchangeResult -> {
                    AccountDto response = exchangeResult.getResponseBody();
                    assertEquals(accounts.get(0), response);
                });
    }

    @Test
    @DisplayName("CreateAccount account")
    public void createAccount() {
        client.post()
                .uri("/v1/accounts")
                .bodyValue("new account")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AccountDto.class)
                .consumeWith(exchangeResult -> {
                    AccountDto response = exchangeResult.getResponseBody();
                    assertEquals(accounts.get(0), response);
                });
    }


    @Test
    @DisplayName("Accounts for a customer")
    public void getAllAccountsForACustomer() {
        client
                .get()
                .uri("/v1/accounts/customer/4")
                .exchange()
                .expectBodyList(AccountDto.class)
                .consumeWith(exchangeResult -> {
                    List<AccountDto> res = exchangeResult.getResponseBody();
                    assertNotNull(res);
                    assertEquals(2, res.size());
                    assertEquals(accounts.get(0), res.get(0));
                    assertEquals(accounts.get(1), res.get(1));
                });

    }

}
