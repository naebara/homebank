package com.bank.integration.account;

import com.bank.exception.ExceptionResponse;
import com.bank.model.dto.AccountDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureWebTestClient
@SpringBootTest
public class AccountControllerIntegrationTest {

    @Autowired
    private WebTestClient client;

    List<AccountDto> accounts = Arrays.asList(
            AccountDto.builder().id(1).customerId(1).currency("EUR").iban("GB82WEST12345698765432").amount(new BigDecimal("20.0")).issuedAt(LocalDate.of(2022, Month.MAY, 7)).build(),
            AccountDto.builder().id(2).customerId(2).currency("DDD").iban("WHAAT").amount(new BigDecimal(100)).issuedAt(LocalDate.now()).build(),
            AccountDto.builder().id(3).customerId(1).currency("EUR").iban("GB91BARC20031863198927").amount(new BigDecimal(300)).issuedAt(LocalDate.of(2023, 1, 2)).build()
    );


    @Test
    @DisplayName("Update account")
    public void updateAccount() {
        AccountDto accountToUpdate = AccountDto.builder()
                .id(accounts.get(0).getId())
                .iban("GB78BARC20035383547217")
                .amount(new BigDecimal("30.0"))
                .issuedAt(LocalDate.of(2020, Month.JANUARY, 3))
                .currency("RON")
                .build();


        client.put()
                .uri("/v1/accounts")
                .bodyValue(accountToUpdate)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AccountDto.class)
                .consumeWith(exchangeResult -> {
                    AccountDto response = exchangeResult.getResponseBody();
                    assertNotNull(response);
                    assertEquals(accountToUpdate, response);
                });
    }


    @Test
    @DisplayName("Update non existing account")
    public void updateNonExistingAccount() {
        AccountDto initialAccount = accounts.get(0);
        initialAccount.setId(77);

        client.put()
                .uri("/v1/accounts")
                .bodyValue(accounts.get(0))
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(ExceptionResponse.class)
                .consumeWith(exchangeResult -> {
                    ExceptionResponse resError = exchangeResult.getResponseBody();
                    assertNotNull(resError);
                    assertNotNull(resError.getErrors());
                    assertEquals(1, resError.getErrors().size());
                    assertNotNull(resError.getDateTime());
                    assertEquals("Account with id 77 was not found!", resError.getErrors().get(0));
                });
    }


    @Test
    @DisplayName("Create account")
    public void createAccount() {
        client.post()
                .uri("/v1/accounts")
                .bodyValue(accounts.get(2))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(AccountDto.class)
                .consumeWith(exchangeResult -> {
                    AccountDto creadtedAccountDtoFromDb = exchangeResult.getResponseBody();
                    assertNotNull(creadtedAccountDtoFromDb);

                    accounts.get(2).setId(creadtedAccountDtoFromDb.getId());

                    assertEquals(creadtedAccountDtoFromDb, accounts.get(2));
                });
    }

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
                    assertEquals(2, response.size());
                });

    }

    @Test
    @DisplayName("Get account by id")
    public void getAccountById_shouldReturnAccountById() {
        client.get()
                .uri("/v1/accounts/1")
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
                .uri("/v1/accounts/2")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Integer.class)
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Delete non existing account by id")
    public void deleteNonExistingAccount() {
        client.delete()
                .uri("/v1/accounts/25")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Integer.class)
                .isEqualTo(0);
    }


    @Test
    @DisplayName("Accounts for a customer")
    public void getAllAccountsForACustomer() {


        client
                .get()
                .uri("/v1/accounts/customer/1")
                .exchange()
                .expectBodyList(AccountDto.class)
                .consumeWith(exchangeResult -> {
                    List<AccountDto> res = exchangeResult.getResponseBody();
                    assertNotNull(res);
                    assertEquals(2, res.size());
                });

    }

    @Test
    @DisplayName("Accounts for a non existing customer")
    public void getAllAccountsForANonExistingCustomer() {

        client
                .get()
                .uri("/v1/accounts/customer/51")
                .exchange()
                .expectBody(ExceptionResponse.class)
                .consumeWith(exchangeResult -> {
                    ExceptionResponse res = exchangeResult.getResponseBody();
                    assertNotNull(res);
                    assertEquals(1, res.getErrors().size());
                    assertEquals("Customer with id 51 was not found!", res.getErrors().get(0));
                });

    }

}
