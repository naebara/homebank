package com.bank.controller;

import com.bank.model.dto.CustomerDto;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/v1/customers")
public class CustomerController {

    List<CustomerDto> customers = Arrays.asList(
            CustomerDto.builder().id(1).fullName("Nae Bara").phoneNumber("123456677").ssn("ddf-335-586").address("Oradea").build(),
            CustomerDto.builder().id(1).fullName("Sergiu Dan").phoneNumber("4356546").ssn("jgkj-674-546").address("Cluj").build(),
            CustomerDto.builder().id(1).fullName("Andreea Dubere").phoneNumber("2353636").ssn("gfd-4442-465").address("Bucuresti").build()
    );

    @GetMapping
    public Flux<CustomerDto> getAllCustomers() {
        return Flux.fromIterable(customers);
    }

    @GetMapping("/{id}")
    public Mono<CustomerDto> getCustomerById(@PathVariable Integer id) {
        return Mono.just(customers.get(0));
    }

    @DeleteMapping("/{id}")
    public Mono<CustomerDto> deleteCustomerById(@PathVariable Integer id) {
        return Mono.just(customers.get(0));
    }

    @PutMapping
    public Mono<CustomerDto> updateCustomer(@RequestBody CustomerDto customerDto) {
        return Mono.just(customers.get(1));
    }

    @PostMapping
    public Mono<CustomerDto> createCustomer(@RequestBody CustomerDto customer) {
        return Mono.just(customer);
    }

}
