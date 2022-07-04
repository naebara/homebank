package com.bank.integration;

import com.bank.model.dto.CustomerDto;
import com.bank.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CustomerControllerIntegrationTest {

    @Autowired
    private WebTestClient client;

    @MockBean
    CustomerService customerService;


    List<CustomerDto> customers = Arrays.asList(
            CustomerDto.builder().id(1).fullName("Nae Bara").phoneNumber("123456677").ssn("123-45-6789").address("Oradea").build(),
            CustomerDto.builder().id(1).fullName("Sergiu Dan").phoneNumber("4356546").ssn("jgkj-674-546").address("Cluj").build(),
            CustomerDto.builder().id(1).fullName("Andreea Dubere").phoneNumber("2353636").ssn("gfd-4442-465").address("Bucuresti").build()
    );

    @Test
    @DisplayName("Get all customers")
    public void getAllClientsTest_ShouldReturnAllCustomers() {

        when(customerService.getAllCustomers()).thenReturn(Flux.fromIterable(customers));

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
                    assertEquals(3, response.size());
                    assertEquals(customers.get(0), response.get(0));
                    assertEquals(customers.get(1), response.get(1));
                    assertEquals(customers.get(2), response.get(2));
                });
    }

    @Test
    @DisplayName("Get customer by id")
    public void getCustomerById_shouldReturnCustomerById() {
        CustomerDto firstCustomer = customers.get(0);
        when(customerService.getCustomerById(3)).thenReturn(Mono.just(firstCustomer));

        client.get()
                .uri("/v1/customers/3")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDto.class)
                .isEqualTo(firstCustomer);
    }

    @Test
    public void getNonExistingCustomer() {
        when(customerService.getCustomerById(3)).thenReturn(Mono.empty());

        client.get()
                .uri("/v1/customers/3")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Mono.class)
                .isEqualTo(null);
    }

    @Test
    @DisplayName("Delete customer by id")
    public void deleteCustomerById() {

        CustomerDto deletedCustomer = customers.get(0);

        when(customerService.deleteUserById(3)).thenReturn(Mono.just(deletedCustomer));

        client.delete()
                .uri("/v1/customers/3")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDto.class)
                .isEqualTo(deletedCustomer);
    }

    @Test
    @DisplayName("Delete non existing customer by id")
    public void deleteNonExistingCustomerById() {

        when(customerService.deleteUserById(3)).thenReturn(Mono.empty());

        client.delete()
                .uri("/v1/customers/3")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Mono.class)
                .isEqualTo(null);
    }

    @Test
    @DisplayName("Update customer")
    public void updateCustomer() {

        CustomerDto initialCustomer = customers.get(0);
        CustomerDto finalCustomer = customers.get(1);

        when(customerService.updateCustomer(initialCustomer)).thenReturn(Mono.just(finalCustomer));

        client.put()
                .uri("/v1/customers")
                .bodyValue(initialCustomer)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDto.class)
                .isEqualTo(finalCustomer);
    }

    @Test
    @DisplayName("Update non existing customer")
    public void updateNonExistingCustomer() {
        CustomerDto initialCustomer = customers.get(0);
        when(customerService.updateCustomer(initialCustomer)).thenReturn(Mono.empty());

        client.put()
                .uri("/v1/customers")
                .bodyValue(initialCustomer)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Mono.class)
                .isEqualTo(null);
    }

    @Test
    @DisplayName("Update customer with invalid information")
    public void updateCustomerWithInvalidInformation() {

        CustomerDto initialCustomer = CustomerDto.builder()
                .id(1)
                .fullName("An")
                .phoneNumber("fgg")
                .ssn("gfd-4442-465")
                .address("dd")
                .build();

        client.put()
                .uri("/v1/customers")
                .bodyValue(initialCustomer)
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(List.class)
                .consumeWith(exchangeResult -> {
                    List<String> responseErrors = exchangeResult.getResponseBody();
                    assertNotNull(responseErrors);
                    assertEquals(4, responseErrors.size());
                    assertTrue(responseErrors.contains("Invalid ssn information"));
                    assertTrue(responseErrors.contains("Invalid phone number"));
                    assertTrue(responseErrors.contains("Full name must be in range (5, 20) characters"));
                    assertTrue(responseErrors.contains("Address must be in range (3, 50) characters"));
                });
    }


    @Test
    @DisplayName("Update customer with null values")
    public void updateCustomerWithNullValuesInformation() {

        CustomerDto initialCustomer = CustomerDto.builder().build();

        client.put()
                .uri("/v1/customers")
                .bodyValue(initialCustomer)
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(List.class)
                .consumeWith(exchangeResult -> {
                    List<String> responseErrors = exchangeResult.getResponseBody();
                    assertNotNull(responseErrors);
                    assertEquals(6, responseErrors.size());
                    assertTrue(responseErrors.contains("Invalid ssn information"));
                    assertTrue(responseErrors.contains("Invalid phone number"));
                    assertTrue(responseErrors.contains("Phone number can not be null"));
                    assertTrue(responseErrors.contains("Address can not be null"));
                    assertTrue(responseErrors.contains("Full name can not be null"));
                    assertTrue(responseErrors.contains("Ssn can not be null"));
                });
    }

    @Test
    @DisplayName("Create customer")
    public void createCustomer() {

        CustomerDto customerDto = customers.get(0);

        when(customerService.createUser(customerDto)).thenReturn(Mono.just(customerDto));

        client.post()
                .uri("/v1/customers")
                .bodyValue(customerDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDto.class)
                .isEqualTo(customerDto);
    }


    @Test
    @DisplayName("Create customer null values")
    public void createCustomerWithMissingInformation() {

        CustomerDto customerDto = CustomerDto.builder().build();

        client.post()
                .uri("/v1/customers")
                .bodyValue(customerDto)
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(List.class)
                .consumeWith(exchangeResult -> {
                    List<String> responseErrors = exchangeResult.getResponseBody();
                    assertNotNull(responseErrors);
                    assertEquals(6, responseErrors.size());
                    assertTrue(responseErrors.contains("Invalid ssn information"));
                    assertTrue(responseErrors.contains("Ssn can not be null"));
                    assertTrue(responseErrors.contains("Address can not be null"));
                    assertTrue(responseErrors.contains("Full name can not be null"));
                    assertTrue(responseErrors.contains("Invalid phone number"));
                    assertTrue(responseErrors.contains("Phone number can not be null"));
                });
    }

    @Test
    @DisplayName("Create customer wrong fullname and address size")
    public void createCustomerWithWrongAddresAndFullNameSizes() {

        CustomerDto customerDto = CustomerDto.builder()
                .ssn("123-45-6789")
                .phoneNumber("2223334444")
                .fullName("a").address("b").build();

        client.post()
                .uri("/v1/customers")
                .bodyValue(customerDto)
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(List.class)
                .consumeWith(exchangeResult -> {
                    List<String> responseErrors = exchangeResult.getResponseBody();
                    assertNotNull(responseErrors);
                    System.out.println(responseErrors);
                    assertEquals(2, responseErrors.size());
                    assertTrue(responseErrors.contains("Address must be in range (3, 50) characters"));
                    assertTrue(responseErrors.contains("Full name must be in range (5, 20) characters"));

                });
    }

}
