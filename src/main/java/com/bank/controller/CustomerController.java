package com.bank.controller;

import com.bank.model.dto.CustomerDto;
import com.bank.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public Flux<CustomerDto> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public Mono<CustomerDto> getCustomerById(@PathVariable Integer id) {
        return customerService.getCustomerById(id);
    }

    @DeleteMapping("/{id}")
    public Mono<CustomerDto> deleteCustomerById(@PathVariable Integer id) {
        return customerService.deleteUserById(id);
    }

    @PutMapping
    public Mono<CustomerDto> updateCustomer(@RequestBody @Valid CustomerDto customerDto) {
        return customerService.updateCustomer(customerDto);
    }

    @PostMapping
    public Mono<CustomerDto> createCustomer(@RequestBody @Valid CustomerDto customerDto) {
        return customerService.createUser(customerDto);
    }

}
