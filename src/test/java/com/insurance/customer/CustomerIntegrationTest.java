package com.insurance.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class CustomerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    // tests...

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnCustomers() throws Exception {

        mockMvc.perform(
                        get("/api/customers")
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void shouldCreateCustomer() throws Exception {

        String requestBody = """
            {
                "firstName": "Max",
                "lastName": "Mustermann",
                "email": "max@example.com",
                "dateOfBirth": "1990-05-10"
            }
            """;

        mockMvc.perform(
                        post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.firstName").value("Max")
                )
                .andExpect(
                        jsonPath("$.lastName").value("Mustermann")
                )
                .andExpect(
                        jsonPath("$.email").value("max@example.com")
                );
    }

    @Test
    void shouldReturnBadRequestWhenCustomerDataIsInvalid() throws Exception {

        String requestBody = """
            {
                "firstName": "",
                "lastName": "Mustermann",
                "email": "not-an-email",
                "dateOfBirth": "1990-05-10"
            }
            """;

        mockMvc.perform(
                        post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.message").value("Validation failed")
                )
                .andExpect(
                        jsonPath("$.errors.firstName").exists()
                )
                .andExpect(
                        jsonPath("$.errors.email").exists()
                );
    }

    @Test
    void shouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {

        mockMvc.perform(
                        get("/api/customers/999999")
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("Customer not found with id: 999999")
                );
    }

}