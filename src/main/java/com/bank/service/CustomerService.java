package com.bank.service;

import com.bank.exception.CustomerNotFoundException;
import com.bank.model.domain.Customer;
import com.bank.model.dto.CustomerDto;
import com.bank.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {

    ModelMapper mapper = new ModelMapper();
    @Autowired
    private CustomerRepository customerRepository;

    public Flux<CustomerDto> getAllCustomers() {
        return customerRepository
                .findAll()
                .map(customer -> mapper.map(customer, CustomerDto.class));
    }

    public Mono<CustomerDto> getCustomerById(Integer id) {
        return customerRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)))
                .map(customer -> mapper.map(customer, CustomerDto.class));
    }

    public Mono<CustomerDto> updateCustomer(CustomerDto customerDto) {
        Customer customer = mapper.map(customerDto, Customer.class);
        return customerRepository.findById(customerDto.getId())
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(customerDto.getId())))
                .flatMap(c -> customerRepository.save(customer))
                .map(c1 -> mapper.map(c1, CustomerDto.class));


    }

    public Mono<CustomerDto> deleteUserById(Integer id) {

        return customerRepository.findById(id)
                .flatMap(c -> customerRepository.deleteById(id).thenReturn(c))
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)))
                .map(c1 -> mapper.map(c1, CustomerDto.class));

    }

    public Mono<CustomerDto> createUser(CustomerDto customerDto) {
        Customer customer = mapper.map(customerDto, Customer.class);
        return customerRepository.save(customer)
                .map(res -> mapper.map(res, CustomerDto.class));
    }
}

