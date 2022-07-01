package com.bank.controller;

import com.bank.model.dto.CustomerDto;
import com.bank.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/v1/customers")
public class CustomerController {

    List<CustomerDto> customers = Arrays.asList(
            CustomerDto.builder().id(1).fullName("Nae Bara").phoneNumber("123456677").ssn("123-45-6789").address("Oradea").build(),
            CustomerDto.builder().id(1).fullName("Sergiu Dan").phoneNumber("4356546").ssn("jgkj-674-546").address("Cluj").build(),
            CustomerDto.builder().id(1).fullName("Andreea Dubere").phoneNumber("2353636").ssn("gfd-4442-465").address("Bucuresti").build()
    );
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
