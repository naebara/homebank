package com.bank.integration;

import com.bank.controller.AccountController;
import com.bank.model.dto.AccountDto;
import com.bank.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@AutoConfigureWebTestClient
@WebFluxTest(controllers = AccountController.class)
public class AccountControllerIntegrationTest {

    @Autowired
    private WebTestClient client;

    @MockBean
    private AccountService accountService;

    List<AccountDto> accounts = Arrays.asList(
            AccountDto.builder().id(1).customerId(1).currency("EUR").iban("GB82 WEST 1234 5698 7654 32").amount(new BigDecimal(400)).issuedAt(LocalDate.now()).build(),
            AccountDto.builder().id(2).customerId(2).currency("DDD").iban("WHAAT").amount(new BigDecimal(100)).issuedAt(LocalDate.now()).build(),
            AccountDto.builder().id(3).customerId(3).currency("RON").iban("asdfkafdhgljasdsdfj").amount(new BigDecimal(300)).issuedAt(LocalDate.now()).build()
    );

    @Test
    @DisplayName("Update account")
    public void updateAccount() {
        when(accountService.getAllAccounts()).thenReturn(Flux.fromIterable(accounts));
        client.put()
                .uri("/v1/accounts")
                .bodyValue(accounts.get(0))
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
    @DisplayName("Update account with invalid values")
    public void updateAccountWithInvalidValues() {
        client.put()
                .uri("/v1/accounts")
                .bodyValue(accounts.get(1))
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(List.class)
                .consumeWith(exchangeResult -> {
                    List<String> errors = exchangeResult.getResponseBody();
                    assertNotNull(errors);
                    System.out.println(errors);
                    assertTrue(errors.contains("Invalid currency"));
                    assertTrue(errors.contains("Invalid iban"));
                });
    }


    @Test
    @DisplayName("Create account")
    public void createAccount() {
        client.post()
                .uri("/v1/accounts")
                .bodyValue(accounts.get(0))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AccountDto.class)
                .consumeWith(exchangeResult -> {
                    AccountDto accountDto = exchangeResult.getResponseBody();
                    assertEquals(accountDto, accounts.get(0));
                });
    }

    @Test
    @DisplayName("Create account with invalid iban")
    public void createAccountWithInvalidIban() {
        client.post()
                .uri("/v1/accounts")
                .bodyValue(accounts.get(2))
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(List.class)
                .consumeWith(exchangeResult -> {
                    List<String> errors = exchangeResult.getResponseBody();
                    assertNotNull(errors);
                    assertEquals(1, errors.size());
                    System.out.println(errors);
                    assertTrue(errors.contains("Invalid iban"));
                });
    }

    @Test
    @DisplayName("Create account with invalid currency")
    public void createAccountWithInvalidCurrency() {

        AccountDto dto = AccountDto.builder()
                .id(1)
                .customerId(1)
                .currency("Halo")
                .iban("GB82 WEST 1234 5698 7654 32")
                .amount(new BigDecimal(400))
                .issuedAt(LocalDate.now()).build();


        client.post()
                .uri("/v1/accounts")
                .bodyValue(dto)
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(List.class)
                .consumeWith(exchangeResult -> {
                    List<String> errors = exchangeResult.getResponseBody();
                    assertNotNull(errors);
                    assertEquals(1, errors.size());
                    assertTrue(errors.contains("Invalid currency"));
                });
    }

    @Test
    @DisplayName("Get all accounts")
    public void getAllAccountsTest_ShouldReturnAllAccounts() {
        when(accountService.getAllAccounts()).thenReturn(Flux.fromIterable(accounts));

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
