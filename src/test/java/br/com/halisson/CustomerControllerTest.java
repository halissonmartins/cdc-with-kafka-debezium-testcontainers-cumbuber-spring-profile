package br.com.halisson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * A Simple Unit Test for CustomerController
 */
@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerController customerController;

    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    void setUp() {
    	
        customer1 = new Customer("John Doe", "john@example.com");
        customer1.setId(1L);

        customer2 = new Customer("Jane Smith", "jane@example.com");
        customer2.setId(2L);
    }

    @Test
    void testGetAllCustomers() {
    	
        List<Customer> customers = Arrays.asList(customer1, customer2);
        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> result = customerController.getAll();

        assertEquals(2, result.size());
        
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("john@example.com", result.get(0).getEmail());
        
        assertEquals("Jane Smith", result.get(1).getName());
        assertEquals("jane@example.com", result.get(1).getEmail());
                
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testGetCustomerById_WhenFound() {
    	
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));

        Customer result = customerController.get(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(customerRepository).findById(1L);
    }

    @Test
    void testGetCustomerById_WhenNotFound() {
    	
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customerController.get(999L));
        verify(customerRepository).findById(999L);
    }

    @Test
    void testSaveCustomer() {
    	
        CustomerInsertionDto dto = new CustomerInsertionDto("Alice Brown", "alice@example.com");

        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(3L);
            return c;
        });

        Customer result = customerController.save(dto);

        assertNotNull(result);
        assertEquals("Alice Brown", result.getName());
        assertEquals("alice@example.com", result.getEmail());
        verify(customerRepository).save(any(Customer.class));
    }
    
    @Test
    void testUpdateCustomer_WhenNotFound() {
    	
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());
        
        CustomerUpdateDto dto = new CustomerUpdateDto(999L, "Alice Brown", "alice@example.com");

        assertThrows(NotFoundException.class, () -> customerController.update(dto));
        verify(customerRepository).findById(999L);
    }
    
    @Test
    void testUpdateCustomer() {
    	
    	when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));
    	
    	CustomerUpdateDto dto = new CustomerUpdateDto(1L, "Alice Brown", "alice@example.com");
    	
    	customerController.update(dto);
    	
    	verify(customerRepository).findById(1L);
    	verify(customerRepository).save(any(Customer.class));
    }
}

