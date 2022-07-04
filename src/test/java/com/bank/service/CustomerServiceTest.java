package com.bank.service;

import com.bank.model.dto.CustomerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerServiceTest {

    @Autowired
    CustomerService customerService;

    @Test
    public void getAllCustomers() {
        Flux<CustomerDto> customers = customerService.getAllCustomers();
        StepVerifier.create(customers)
                .expectNext(CustomerDto.builder().id(1).fullName("Dan Badea1").address("Mures").phoneNumber("2224445558").ssn("343-25-5859").build())
                .expectNext(CustomerDto.builder().id(2).fullName("Sergiu Gal1").address("Satu Mare").phoneNumber("46645345").ssn("354-12-7742").build())
                .verifyComplete();


        StepVerifier.create(customers)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void getCustomerById() {
        Mono<CustomerDto> customer = customerService.getCustomerById(1);
        StepVerifier.create(customer)
                .expectNext(CustomerDto.builder().id(1).fullName("Dan Badea1").address("Mures").phoneNumber("2224445558").ssn("343-25-5859").build())
                .verifyComplete();
    }

    @Test
    public void getCustomerById_WhenCustomerDoesNotExist() {
        Mono<CustomerDto> customer = customerService.getCustomerById(14);
        StepVerifier.create(customer).verifyComplete();
    }

    @Test
    public void getCustomerById_IdIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            customerService.getCustomerById(null);
        });

        assertEquals("Id must not be null!", ex.getMessage());
    }

    @Test
    public void updateCustomer() {
        CustomerDto userPresentInDbWithId1AndDifferentValues = CustomerDto.builder().id(1).fullName("Nae Bara").address("Cluj").phoneNumber("123456789").ssn("222-11-4444").build();
        Mono<CustomerDto> updatedCustomerFromDb = customerService.updateCustomer(userPresentInDbWithId1AndDifferentValues);

        StepVerifier.create(updatedCustomerFromDb)
                .expectNext(userPresentInDbWithId1AndDifferentValues)
                .verifyComplete();

    }

    @Test
    public void updateNonExistingCustomer() {
        CustomerDto userPresentInDbWithId1AndDifferentValues = CustomerDto.builder().id(14).fullName("Nae Bara").address("Cluj").phoneNumber("123456789").ssn("222-11-4444").build();
        Mono<CustomerDto> updatedCustomerFromDb = customerService.updateCustomer(userPresentInDbWithId1AndDifferentValues);

        StepVerifier.create(updatedCustomerFromDb)
                .verifyComplete();
    }

    @Test
    public void deleteCustomer() {
        Mono<CustomerDto> deletedUser = customerService.deleteUserById(2);
        StepVerifier.create(deletedUser)
                .expectNext(CustomerDto.builder().id(2).fullName("Sergiu Gal1").address("Satu Mare").phoneNumber("46645345").ssn("354-12-7742").build())
                .verifyComplete();
    }

    @Test
    public void deleteCustomerIdIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> customerService.deleteUserById(null));
        assertEquals("Id must not be null!", ex.getMessage());

    }

    @Test
    public void deleteNonExistingCustomer() {
        Mono<CustomerDto> deletedUser = customerService.deleteUserById(15);
        StepVerifier.create(deletedUser)
                .verifyComplete();
    }


    @Test
    public void createCustomer() {
        CustomerDto customer = CustomerDto.builder().fullName("Nae Bara").address("Cluj").phoneNumber("1111222233").ssn("111-22-3333").build();
        Mono<CustomerDto> response = customerService.createUser(customer);

        customer.setId(3);

        StepVerifier.create(response)
                .expectNext(customer)
                .verifyComplete();
    }


}
