package com.bank.integration.customer;

import com.bank.exception.ExceptionResponse;
import com.bank.model.dto.CustomerDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerControllerIntegrationTest {

    @Autowired
    private WebTestClient client;

    List<CustomerDto> customers = Arrays.asList(
            CustomerDto.builder().id(1).fullName("Dan Badea1").phoneNumber("2224445558").ssn("343-25-5859").address("Mures").build(),
            CustomerDto.builder().id(2).fullName("Sergiu Gal1").phoneNumber("46645345").ssn("354-12-7742").address("Satu Mare").build(),
            CustomerDto.builder().id(1).fullName("Andreea Dubere").phoneNumber("2353636").ssn("gfd-4442-465").address("Bucuresti").build()
    );

    @Test
    @DisplayName("Get all customers")
    public void getAllClientsTest_ShouldReturnAllCustomers() {
        client
                .get()
                .uri("/v1/customers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CustomerDto.class)
                .consumeWith(exhangeResult -> {
                    List<CustomerDto> response = exhangeResult.getResponseBody();
                    assertNotNull(response);
                    assertEquals(2, response.size());
                });
    }

    @Test
    @DisplayName("Get customer by id")
    public void getCustomerById_shouldReturnCustomerById() {
        CustomerDto firstCustomer = customers.get(0);

        client.get()
                .uri("/v1/customers/1")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDto.class)
                .isEqualTo(firstCustomer);
    }

    @Test
    public void getNonExistingCustomer() {

        client.get()
                .uri("/v1/customers/3")
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(ExceptionResponse.class)
                .consumeWith(exchangeResult -> {
                    ExceptionResponse responseError = exchangeResult.getResponseBody();
                    assertNotNull(responseError);
                    assertEquals(1, responseError.getErrors().size());
                    assertEquals("Customer with id 3 was not found!", responseError.getErrors().get(0));
                });
    }

    @Test
    @DisplayName("Delete customer by id")
    public void deleteCustomerById() {

        CustomerDto deletedCustomer = customers.get(1);


        client.delete()
                .uri("/v1/customers/2")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDto.class)
                .isEqualTo(deletedCustomer);
    }

    @DisplayName("Delete customer with associated accounts, should return error")
    @Test
    public void deleteCustomerWithAssociatedAccounts() {
        client.delete()
                .uri("/v1/customers/1")
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(ExceptionResponse.class)
                .consumeWith(exchangeResult -> {
                    ExceptionResponse response = exchangeResult.getResponseBody();
                    assertNotNull(response);
                    assertEquals(1, response.getErrors().size());
                    assertTrue(response.getErrors().get(0).contains("Referential integrity constraint violation"));
                    assertTrue(response.getErrors().get(0).contains("UBLIC.ACCOUNTS FOREIGN KEY(CUSTOMER_ID) REFERENCES PUBLIC.CUSTOMER(ID)"));
                });
    }

    @Test
    @DisplayName("Delete non existing customer by id")
    public void deleteNonExistingCustomerById() {


        client.delete()
                .uri("/v1/customers/35")
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(ExceptionResponse.class)
                .consumeWith(exchangeResult -> {
                    ExceptionResponse response = exchangeResult.getResponseBody();
                    assertNotNull(response);
                    assertEquals(1, response.getErrors().size());
                    assertEquals("Customer with id 35 was not found!", response.getErrors().get(0));
                });
    }

    @Test
    @DisplayName("Update customer")
    public void updateCustomer() {

        CustomerDto initialCustomer = customers.get(0);
        initialCustomer.setAddress("Dubai");
        initialCustomer.setPhoneNumber("1234567890");
        initialCustomer.setFullName("Natanael Bara");
        initialCustomer.setSsn("481-74-3452");


        client.put()
                .uri("/v1/customers")
                .bodyValue(initialCustomer)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDto.class)
                .isEqualTo(initialCustomer);
    }

    @Test
    @DisplayName("Update non existing customer")
    public void updateNonExistingCustomer() {
        CustomerDto initialCustomer = customers.get(0);
        initialCustomer.setId(55);

        client.put()
                .uri("/v1/customers")
                .bodyValue(initialCustomer)
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(ExceptionResponse.class)
                .consumeWith(exchangeResult -> {
                    ExceptionResponse response = exchangeResult.getResponseBody();
                    assertNotNull(response);
                    assertEquals(1, response.getErrors().size());
                    assertEquals("Customer with id 55 was not found!", response.getErrors().get(0));
                    assertNotNull(response.getDateTime());
                });
    }


    @Test
    @DisplayName("Create customer")
    public void createCustomer() {

        CustomerDto customerDto = CustomerDto
                .builder()
                .ssn("343-25-5859")
                .phoneNumber("444555543")
                .address("Grecia")
                .fullName("Andrei Marius")
                .build();


        client.post()
                .uri("/v1/customers")
                .bodyValue(customerDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDto.class)
                .consumeWith(exchangeResult -> {
                    CustomerDto createdCustomer = exchangeResult.getResponseBody();
                    assertNotNull(createdCustomer);
                    assertNotNull(createdCustomer.getId());
                    customerDto.setId(createdCustomer.getId());
                    assertEquals(customerDto, createdCustomer);
                });
    }

}
