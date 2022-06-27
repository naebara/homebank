package com.bank.controller;

import com.bank.model.dto.AccountDto;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

    List<AccountDto> accounts = Arrays.asList(
            AccountDto.builder().id(1).customerId(1).number("asdfkljasdsdfj").amount(new BigDecimal(400)).issuedAt(LocalDate.now()).build(),
            AccountDto.builder().id(2).customerId(2).number("asdagfkljasdsdfj").amount(new BigDecimal(100)).issuedAt(LocalDate.now()).build(),
            AccountDto.builder().id(3).customerId(3).number("asdfkafdhgljasdsdfj").amount(new BigDecimal(300)).issuedAt(LocalDate.now()).build()
    );

    @GetMapping
    public Flux<AccountDto> getAllAccounts() {
        return Flux.fromIterable(accounts);
    }

    @GetMapping("/{id}")
    public Mono<AccountDto> getAccountById(@PathVariable Integer id) {
        return Mono.just(accounts.get(0));
    }

    @DeleteMapping("/{id}")
    public Mono<AccountDto> deleteAccountById(@PathVariable Integer id) {
        return Mono.just(accounts.get(0));
    }

    @PutMapping
    public Mono<AccountDto> updateAccount(@RequestBody String account) {
        return Mono.just(accounts.get(0));
    }

    @PostMapping
    public Mono<AccountDto> createAccount(@RequestBody String account) {
        return Mono.just(accounts.get(0));
    }

    @GetMapping("/customer/{customerId}")
    public Flux<AccountDto> getAllAccountsForCustomer(@PathVariable Integer customerId) {
        return Flux.just(accounts.get(0), accounts.get(1));
    }

}
