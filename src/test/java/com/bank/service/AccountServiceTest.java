package com.bank.service;

import com.bank.model.dto.AccountDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // spin up la context pt fiecare test
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;


    @Test
    public void getAllAccounts() {
        Flux<AccountDto> accounts = accountService.getAllAccounts().log();

        StepVerifier.create(accounts)
                .expectNextCount(2)
                .verifyComplete();

    }
}
