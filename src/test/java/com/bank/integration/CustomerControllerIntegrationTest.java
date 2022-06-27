package com.bank.integration;

import com.bank.model.dto.CustomerDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerIntegrationTest {

    @Autowired
    private WebTestClient client;

    List<CustomerDto> customers = Arrays.asList(
            CustomerDto.builder().id(1).fullName("Nae Bara").phoneNumber("123456677").ssn("ddf-335-586").address("Oradea").build(),
            CustomerDto.builder().id(1).fullName("Sergiu Dan").phoneNumber("4356546").ssn("jgkj-674-546").address("Cluj").build(),
            CustomerDto.builder().id(1).fullName("Andreea Dubere").phoneNumber("2353636").ssn("gfd-4442-465").address("Bucuresti").build()
    );

    @Test
    @DisplayName("Get all customers")
    public void getAllClientsTest_ShouldReturnAllClients() {

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

        client.get()
                .uri("/v1/customers/3")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDto.class)
                .isEqualTo(firstCustomer);
    }

    @Test
    @DisplayName("Delete customer by id")
    public void deleteCustomerById() {

        CustomerDto deletedCustomer = customers.get(0);

        client.delete()
                .uri("/v1/customers/3")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDto.class)
                .isEqualTo(deletedCustomer);
    }


    @Test
    @DisplayName("Update customer")
    public void updateCustomer() {

        CustomerDto initialCustomer = customers.get(0);
        CustomerDto finalCustomer = customers.get(1);

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
    @DisplayName("Create customer")
    public void createCustomer() {

        CustomerDto customerDto = customers.get(0);

        client.post()
                .uri("/v1/customers")
                .bodyValue(customerDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDto.class)
                .isEqualTo(customerDto);
    }

}
