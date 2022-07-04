package com.bank.service;

import com.bank.model.dto.AccountDto;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // spin up la context pt fiecare test
public class AccountServiceTest {

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
                .verifyComplete();
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
}
