package com.insurance.customer.service;

import com.insurance.customer.dto.CustomerRequest;
import com.insurance.customer.dto.CustomerResponse;
import com.insurance.customer.entity.Customer;
import com.insurance.customer.mapper.CustomerMapper;
import com.insurance.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.insurance.customer.exception.CustomerNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldReturnCustomerById() {

        // Arrange
        Long customerId = 1L;

        Customer customer = new Customer();
        customer.setFirstName("Max");
        customer.setLastName("Mustermann");
        customer.setEmail("max@example.com");
        customer.setDateOfBirth(
                LocalDate.of(1990, 5, 15)
        );

        CustomerResponse response = new CustomerResponse();
        response.setId(customerId);
        response.setFirstName("Max");
        response.setLastName("Mustermann");
        response.setEmail("max@example.com");
        response.setDateOfBirth(
                LocalDate.of(1990, 5, 15)
        );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(customerMapper.toResponse(customer))
                .thenReturn(response);

        // Act
        CustomerResponse result =
                customerService.getCustomerById(customerId);

        // Assert
        assertEquals(customerId, result.getId());
        assertEquals("Max", result.getFirstName());
        assertEquals("Mustermann", result.getLastName());
        assertEquals("max@example.com", result.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenCustomerDoesNotExist() {

        // Arrange
        Long customerId = 999L;

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        // Act & Assert
        CustomerNotFoundException exception =
                assertThrows(
                        CustomerNotFoundException.class,
                        () -> customerService.getCustomerById(customerId)
                );

        assertEquals(
                "Customer not found with id: 999",
                exception.getMessage()
        );

        // Verify
        verify(customerRepository).findById(customerId);
    }

    @Test
    void shouldCreateCustomer() {

        // Arrange
        CustomerRequest request = new CustomerRequest();
        request.setFirstName("Max");
        request.setLastName("Mustermann");
        request.setEmail("max@example.com");
        request.setDateOfBirth(LocalDate.of(1990, 5, 10));

        Customer customer = new Customer();
        customer.setFirstName("Max");
        customer.setLastName("Mustermann");
        customer.setEmail("max@example.com");
        customer.setDateOfBirth(LocalDate.of(1990, 5, 10));

        CustomerResponse response = new CustomerResponse();
        response.setFirstName("Max");
        response.setLastName("Mustermann");
        response.setEmail("max@example.com");
        response.setDateOfBirth(LocalDate.of(1990, 5, 10));

        when(customerMapper.toEntity(request))
                .thenReturn(customer);

        when(customerRepository.save(customer))
                .thenReturn(customer);

        when(customerMapper.toResponse(customer))
                .thenReturn(response);

        // Act
        CustomerResponse result =
                customerService.createCustomer(request);

        // Assert
        assertEquals("Max", result.getFirstName());
        assertEquals("Mustermann", result.getLastName());
        assertEquals("max@example.com", result.getEmail());

        // Verify
        verify(customerMapper).toEntity(request);
        verify(customerRepository).save(customer);
        verify(customerMapper).toResponse(customer);
    }

    @Test
    void shouldUpdateCustomer() {

        // Arrange
        Long customerId = 1L;

        CustomerRequest request = new CustomerRequest();
        request.setFirstName("Max");
        request.setLastName("Mustermann");
        request.setEmail("new@example.com");
        request.setDateOfBirth(LocalDate.of(1990, 5, 10));

        Customer customer = new Customer();
        customer.setFirstName("Old");
        customer.setLastName("Name");
        customer.setEmail("old@example.com");
        customer.setDateOfBirth(LocalDate.of(1980, 1, 1));

        CustomerResponse response = new CustomerResponse();
        response.setId(customerId);
        response.setFirstName("Max");
        response.setLastName("Mustermann");
        response.setEmail("new@example.com");
        response.setDateOfBirth(LocalDate.of(1990, 5, 10));

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(customerRepository.save(customer))
                .thenReturn(customer);

        when(customerMapper.toResponse(customer))
                .thenReturn(response);

        // Act
        CustomerResponse result =
                customerService.updateCustomer(customerId, request);

        // Assert
        assertEquals("Max", result.getFirstName());
        assertEquals("Mustermann", result.getLastName());
        assertEquals("new@example.com", result.getEmail());

        // Verify
        verify(customerRepository).findById(customerId);
        verify(customerRepository).save(customer);
        verify(customerMapper).toResponse(customer);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingCustomer() {

        // Arrange
        Long customerId = 999L;

        CustomerRequest request = new CustomerRequest();
        request.setFirstName("Max");
        request.setLastName("Mustermann");
        request.setEmail("max@example.com");
        request.setDateOfBirth(LocalDate.of(1990, 5, 10));

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        // Act & Assert
        CustomerNotFoundException exception =
                assertThrows(
                        CustomerNotFoundException.class,
                        () -> customerService.updateCustomer(
                                customerId,
                                request
                        )
                );

        assertEquals(
                "Customer not found with id: 999",
                exception.getMessage()
        );

        // Verify
        verify(customerRepository).findById(customerId);
    }

    @Test
    void shouldReturnAllCustomers() {

        // Arrange
        Customer customer1 = new Customer();
        customer1.setFirstName("Max");
        customer1.setLastName("Mustermann");
        customer1.setEmail("max@example.com");
        customer1.setDateOfBirth(LocalDate.of(1990, 5, 10));

        Customer customer2 = new Customer();
        customer2.setFirstName("Anna");
        customer2.setLastName("Musterfrau");
        customer2.setEmail("anna@example.com");
        customer2.setDateOfBirth(LocalDate.of(1992, 8, 20));

        CustomerResponse response1 = new CustomerResponse();
        response1.setFirstName("Max");
        response1.setLastName("Mustermann");
        response1.setEmail("max@example.com");
        response1.setDateOfBirth(LocalDate.of(1990, 5, 10));

        CustomerResponse response2 = new CustomerResponse();
        response2.setFirstName("Anna");
        response2.setLastName("Musterfrau");
        response2.setEmail("anna@example.com");
        response2.setDateOfBirth(LocalDate.of(1992, 8, 20));

        when(customerRepository.findAll())
                .thenReturn(List.of(customer1, customer2));

        when(customerMapper.toResponse(customer1))
                .thenReturn(response1);

        when(customerMapper.toResponse(customer2))
                .thenReturn(response2);

        // Act
        List<CustomerResponse> result =
                customerService.getAllCustomers();

        // Assert
        assertEquals(2, result.size());

        assertEquals(
                "Max",
                result.get(0).getFirstName()
        );

        assertEquals(
                "Anna",
                result.get(1).getFirstName()
        );

        // Verify
        verify(customerRepository).findAll();
        verify(customerMapper).toResponse(customer1);
        verify(customerMapper).toResponse(customer2);
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoCustomers() {

        // Arrange
        when(customerRepository.findAll())
                .thenReturn(List.of());

        // Act
        List<CustomerResponse> result =
                customerService.getAllCustomers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify
        verify(customerRepository).findAll();
    }
}