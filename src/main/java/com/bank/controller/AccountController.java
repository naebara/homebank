package com.bank.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

    @GetMapping
    public Flux<String> getAllAccounts() {
        return Flux.just("All accounts");
    }

    @GetMapping("/{id}")
    public Mono<String> getCustomerById(@PathVariable Integer id) {
        return Mono.just("Account with id " + id);
    }

    @DeleteMapping("/{id}")
    public Mono<String> deleteCustomerById(@PathVariable Integer id) {
        return Mono.just("Deleted account with id " + id);
    }

    @PutMapping
    public Mono<String> updateCustomer(@RequestBody String user) {
        return Mono.just("Updated account : " + user);
    }

}
