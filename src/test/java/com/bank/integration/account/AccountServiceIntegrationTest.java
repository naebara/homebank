package com.bank.integration.account;

import com.bank.model.dto.AccountDto;
import com.bank.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AccountServiceIntegrationTest {

    @Autowired
    private AccountService accountService;

    AccountDto first = AccountDto.builder()
            .id(1)
            .iban("GB82WEST12345698765432")
            .currency("EUR")
            .customerId(1)
            .amount(new BigDecimal("20.0"))
            .issuedAt(LocalDate.of(2022, Month.MAY, 7))
            .build();

    AccountDto second = AccountDto.builder()
            .id(2)
            .iban("GB03BARC20038041157768")
            .currency("RON")
            .customerId(1)
            .amount(new BigDecimal("10.0"))
            .issuedAt(LocalDate.of(2022, Month.MAY, 22))
            .build();

    @Test
    public void getAllAccounts() {
        Flux<AccountDto> accounts = accountService.getAllAccounts().log();

        StepVerifier.create(accounts)
                .expectNextCount(2)
                .verifyComplete();


        StepVerifier.create(accounts)
                .expectNext(first, second)
                .verifyComplete();
    }

    @Test
    public void getAccountById() {
        Mono<AccountDto> responseAccount = accountService.getById(1);
        assertNotNull(responseAccount);
        StepVerifier.create(responseAccount)
                .expectNext(first)
                .verifyComplete();
    }

    @Test
    public void getAccountByIdWhenIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.getById(null);

        });

        assertNotNull(exception);
        assertEquals("Value must not be null!", exception.getMessage());
    }

    @Test
    public void getNonExistingAccount() {
        Mono<AccountDto> responseAccount = accountService.getById(55);
        StepVerifier.create(responseAccount)
                .verifyErrorMessage("Account with id 55 was not found!");
    }

    @Test
    public void deleteAccountById() {
        Mono<Integer> deletedAccount = accountService.deleteAccountById(1);
        StepVerifier.create(deletedAccount)
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    public void deleteNonExistingAccount() {
        Mono<Integer> deletedAccount = accountService.deleteAccountById(155);
        StepVerifier.create(deletedAccount)
                .expectNext(0)
                .verifyComplete();
    }

    @Test
    public void updateAccount() {

        first.setAmount(new BigDecimal(700));
        first.setCurrency("RON");
        first.setIssuedAt(LocalDate.of(2021, Month.JANUARY, 21));
        first.setIban("GB49BARC20039567466878");
        first.setCustomerId(2);

        Mono<AccountDto> updated = accountService.updateAccount(first);
        StepVerifier.create(updated)
                .expectNext(first)
                .verifyComplete();
    }

    @Test
    public void updateNonExistingAccount() {
        first.setId(55);
        first.setAmount(new BigDecimal(700));
        first.setCurrency("RON");
        first.setIssuedAt(LocalDate.of(2021, Month.JANUARY, 21));
        first.setIban("GB49BARC20039567466878");
        first.setCustomerId(2);

        Mono<AccountDto> updated = accountService.updateAccount(first);
        StepVerifier.create(updated)
                .verifyErrorMessage("Account with id 55 was not found!");
    }

    @Test
    public void updateAccountSetNonExistingCustomerId() {

        first.setAmount(new BigDecimal(700));
        first.setCurrency("RON");
        first.setIssuedAt(LocalDate.of(2021, Month.JANUARY, 21));
        first.setIban("GB49BARC20039567466878");
        first.setCustomerId(6);


        Mono<AccountDto> updatedAccount = accountService.updateAccount(first);

        StepVerifier.create(updatedAccount)
                .verifyErrorMessage("Customer with id 6 was not found!");
    }

    @Test
    public void createNewAccount() {
        AccountDto newAccountInfo = AccountDto.builder()
                .iban("GB37BARC20038472725482")
                .currency("EUR")
                .customerId(2)
                .amount(new BigDecimal("700.0"))
                .issuedAt(LocalDate.of(2000, Month.JANUARY, 1))
                .build();

        Mono<AccountDto> responseAccountDto = accountService.createAccount(newAccountInfo);

        responseAccountDto.subscribe(res -> {
            assertEquals(3, res.getId());
            assertEquals(newAccountInfo.getIban(), res.getIban());
            assertEquals(newAccountInfo.getAmount(), res.getAmount());
            assertEquals(newAccountInfo.getCurrency(), res.getCurrency());
            assertEquals(newAccountInfo.getCustomerId(), res.getCustomerId());
            assertEquals(newAccountInfo.getIssuedAt(), res.getIssuedAt());
        });
    }

    @Test
    public void getAccountsForCustomer() {
        Flux<AccountDto> accountsForCustomer = accountService.getAccountsForCustomer(1);
        StepVerifier.create(accountsForCustomer)
                .expectNext(first, second)
                .verifyComplete();
    }

    @Test
    public void getAccountsForNonExistingCustomer() {
        Flux<AccountDto> accountsForCustomer = accountService.getAccountsForCustomer(14);
        StepVerifier.create(accountsForCustomer)
                .verifyErrorMessage("Customer with id 14 was not found!");
    }
}
