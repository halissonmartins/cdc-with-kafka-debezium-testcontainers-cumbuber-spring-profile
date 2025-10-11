package br.com.halisson;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * A simple CRUD controller
 * 
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	private final CustomerRepository repo;

	CustomerController(CustomerRepository repo) {
		this.repo = repo;
	}

	@GetMapping
	List<Customer> getAll() {
		return repo.findAll();
	}
	
	@GetMapping("/{id}")
	Customer get(@PathVariable(name = "id") final Long id) {
		Optional<Customer> optionalCustomer = repo.findById(id);
		return optionalCustomer.orElseThrow(NotFoundException::new);
	}	
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	Customer save(@RequestBody final CustomerInsertionDto dto) {
		Customer customer = new Customer(dto.name(), dto.email());
		
		return repo.save(customer);
	}
	
	@PutMapping
	Customer update(@RequestBody final CustomerUpdateDto dto) {
		
		Optional<Customer> optionalCustomer = repo.findById(dto.id());
		
		Customer customer = optionalCustomer.orElseThrow(NotFoundException::new);
				
		customer.setName(dto.name());
		customer.setEmail(dto.email());
		
		return repo.save(customer);
	}
}