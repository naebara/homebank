package com.bank.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/customer")
public class CustomerController {

    @GetMapping
    public Flux<String> getAllCustomers() {
        return Flux.just("All customers");
    }

    @GetMapping("/{id}")
    public Mono<String> getCustomerById(@PathVariable Integer id) {
        return Mono.just("User with id " + id);
    }

    @DeleteMapping("/{id}")
    public Mono<String> deleteCustomerById(@PathVariable Integer id){
        return Mono.just("Deleted user with id " + id);
    }

    @PutMapping
    public Mono<String> updateCustomer(@RequestBody String user){
        return Mono.just("Updated user : " + user);
    }


}
