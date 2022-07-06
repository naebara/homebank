package com.bank.integration;

import com.bank.controller.AccountController;
import com.bank.exception.ExceptionResponse;
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
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
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
        AccountDto initialAccount = accounts.get(0);
        AccountDto updatedAccount = AccountDto.builder()
                .id(initialAccount.getId())
                .iban("GB78BARC20035383547217")
                .amount(new BigDecimal("30.0"))
                .issuedAt(LocalDate.of(2020, Month.JANUARY, 3))
                .currency("RON")
                .build();

        when(accountService.updateAccount(initialAccount)).thenReturn(Mono.just(updatedAccount));

        client.put()
                .uri("/v1/accounts")
                .bodyValue(accounts.get(0))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AccountDto.class)
                .consumeWith(exchangeResult -> {
                    AccountDto response = exchangeResult.getResponseBody();
                    assertNotNull(response);
                    assertEquals(updatedAccount, response);
                });
    }

    @Test
    @DisplayName("Update non existing account")
    public void updateNonExistingAccount() {
        AccountDto initialAccount = accounts.get(0);

        when(accountService.updateAccount(initialAccount)).thenReturn(Mono.empty());

        client.put()
                .uri("/v1/accounts")
                .bodyValue(accounts.get(0))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Mono.class)
                .isEqualTo(null);
    }

    @Test
    @DisplayName("Update account with invalid iban and currency")
    public void updateAccountWithInvalidValues() {
        client.put()
                .uri("/v1/accounts")
                .bodyValue(accounts.get(1))
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(ExceptionResponse.class)
                .consumeWith(exchangeResult -> {
                    ExceptionResponse exception = exchangeResult.getResponseBody();

//                    assertNotNull(errors);
//                    System.out.println(errors);
//                    assertTrue(errors.contains("Invalid currency"));
//                    assertTrue(errors.contains("Invalid iban"));
                });
    }


    @Test
    @DisplayName("Create account")
    public void createAccount() {
        when(accountService.createAccount(accounts.get(0))).thenReturn(Mono.just(accounts.get(0)));
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
                .expectBody(ExceptionResponse.class)
                .consumeWith(exchangeResult -> {
                    ExceptionResponse exceptionResponse = exchangeResult.getResponseBody();
                    assertNotNull(exceptionResponse);
                    assertEquals(1, exceptionResponse.getErrors().size());
                    assertTrue(exceptionResponse.getErrors().contains("Invalid iban"));
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
                .expectBody(ExceptionResponse.class)
                .consumeWith(exchangeResult -> {
                    ExceptionResponse exceptionResponse = exchangeResult.getResponseBody();
                    assertNotNull(exceptionResponse);
                    assertEquals(1, exceptionResponse.getErrors().size());
                    assertTrue(exceptionResponse.getErrors().contains("Invalid currency"));
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
        when(accountService.getById(3)).thenReturn(Mono.just(accounts.get(0)));
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
        when(accountService.deleteAccountById(3)).thenReturn(Mono.just(1));
        client.delete()
                .uri("/v1/accounts/3")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Integer.class)
                .isEqualTo(1);
    }


    @Test
    @DisplayName("Accounts for a customer")
    public void getAllAccountsForACustomer() {

        when(accountService.getAccountsForCustomer(3)).thenReturn(Flux.just(accounts.get(0), accounts.get(1)));

        client
                .get()
                .uri("/v1/accounts/customer/3")
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
