package com.bank.unit.customer;

import com.bank.model.domain.Customer;
import com.bank.model.dto.CustomerDto;
import com.bank.repository.CustomerRepository;
import com.bank.service.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerServiceTest {

    @InjectMocks
    CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    List<Customer> customers = Arrays.asList(
            Customer.builder().id(1).fullName("Nae Bara").phoneNumber("123456677").ssn("123-45-6789").address("Oradea").build(),
            Customer.builder().id(1).fullName("Sergiu Dan").phoneNumber("4356546").ssn("jgkj-674-546").address("Cluj").build(),
            Customer.builder().id(1).fullName("Andreea Dubere").phoneNumber("2353636").ssn("gfd-4442-465").address("Bucuresti").build()
    );


    List<CustomerDto> customersDto = Arrays.asList(
            CustomerDto.builder().id(1).fullName("Nae Bara").phoneNumber("123456677").ssn("123-45-6789").address("Oradea").build(),
            CustomerDto.builder().id(1).fullName("Sergiu Dan").phoneNumber("4356546").ssn("jgkj-674-546").address("Cluj").build(),
            CustomerDto.builder().id(1).fullName("Andreea Dubere").phoneNumber("2353636").ssn("gfd-4442-465").address("Bucuresti").build()
    );

    @Test
    public void getAllCustomers() {
        when(customerRepository.findAll()).thenReturn(Flux.fromIterable(customers));
        Flux<CustomerDto> responseCustomers = customerService.getAllCustomers();

        StepVerifier.create(responseCustomers)
                .expectNext(customersDto.get(0), customersDto.get(1), customersDto.get(2))
                .verifyComplete();
    }

    @Test
    public void getCustomerById() {

        when(customerRepository.findById(1)).thenReturn(Mono.just(customers.get(0)));

        Mono<CustomerDto> customer = customerService.getCustomerById(1);

        StepVerifier.create(customer)
                .expectNext(customersDto.get(0))
                .verifyComplete();
    }

    @Test
    public void getCustomerById_WhenCustomerDoesNotExist() {
        when(customerRepository.findById(14)).thenReturn(Mono.empty());
        Mono<CustomerDto> customer = customerService.getCustomerById(14);
        StepVerifier.create(customer)
                .verifyErrorMessage("Customer with id 14 was not found!");
    }

    @Test
    public void getCustomerById_IdIsNull() {

        when(customerRepository.findById((Integer) any())).thenThrow(new IllegalArgumentException("Id must not be null!"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            customerService.getCustomerById(null);
        });

        Assertions.assertNotNull(ex);
        assertEquals("Id must not be null!", ex.getMessage());
    }

    @Test
    public void updateCustomer() {

        when(customerRepository.findById(1)).thenReturn(Mono.just(customers.get(0)));
        when(customerRepository.save(customers.get(0))).thenReturn(Mono.just(customers.get(1)));

        Mono<CustomerDto> response = customerService.updateCustomer(customersDto.get(0));

        StepVerifier.create(response)
                .expectNext(customersDto.get(1))
                .verifyComplete();
    }

    @Test
    public void updateNonExistingCustomer() {

        when(customerRepository.findById(1)).thenReturn(Mono.empty());
        Mono<CustomerDto> updatedCustomerFromDb = customerService.updateCustomer(customersDto.get(0));

        StepVerifier.create(updatedCustomerFromDb)
                .verifyErrorMessage("Customer with id 1 was not found!");
    }


    @Test
    public void deleteCustomer() {

        when(customerRepository.findById(2)).thenReturn(Mono.just(customers.get(1)));
        when(customerRepository.deleteById(2)).thenReturn(Mono.empty());

        Mono<CustomerDto> deletedUser = customerService.deleteUserById(2);
        StepVerifier.create(deletedUser)
                .expectNext(customersDto.get(1))
                .verifyComplete();
    }

    @Test
    public void deleteCustomerIdIsNull() {
        when(customerRepository.findById((Integer) any())).thenThrow(new IllegalArgumentException("Id must not be null!"));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> customerService.deleteUserById(null));
        assertEquals("Id must not be null!", ex.getMessage());
    }

    @Test
    public void deleteNonExistingCustomer() {
        when(customerRepository.findById(15)).thenReturn(Mono.empty());
        Mono<CustomerDto> deletedUser = customerService.deleteUserById(15);
        StepVerifier.create(deletedUser)
                .verifyErrorMessage("Customer with id 15 was not found!");
    }


    @Test
    public void createCustomer() {
        Customer c = customers.get(0);
        when(customerRepository.save(any())).thenReturn(Mono.just(c));

        Mono<CustomerDto> response = customerService.createUser(new CustomerDto());
        StepVerifier.create(response)
                .expectNext(customersDto.get(0))
                .verifyComplete();
    }


}
