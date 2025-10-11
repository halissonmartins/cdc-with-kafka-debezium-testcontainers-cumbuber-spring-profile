package br.com.halisson.bbd.steps;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.springframework.http.HttpStatus;

import br.com.halisson.Customer;
import br.com.halisson.CustomerInsertionDto;
import br.com.halisson.CustomerRepository;
import br.com.halisson.CustomerUpdateDto;
import br.com.halisson.bdd.config.CucumberSpringConfiguration;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomerSteps extends CucumberSpringConfiguration{
	
	private static final String API_CUSTOMERS_PATH = "/customers";

	private final CustomerRepository customerRepository;

	private Response response;
	
	//Commons
	@Given("I have {int} customers included")
	public void have_customers_included(Integer qtd) {
		assertEquals(qtd, customerRepository.findAll().size());
		log.info("Has {} customers included");
	}
	
	@Then("Should be displayed a NotFound status")
	public void should_be_displayed_a_not_found_status() {
		response
			.then()
				.statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	@Given("I have a customer with ID {long} that not exists")
	public void have_customer_with_id_that_not_exists(Long id) {
		
		Optional<Customer> optionalCustomer = customerRepository.findById(id);
		assertFalse(optionalCustomer.isPresent());
	}
	
	//Scenario: Getting a information about all customers	
	@When("I get all customers")
	public void get_all_customers() {
		response = given(buildRequestSpecification()).contentType(ContentType.JSON)
			.when()
				.get(API_CUSTOMERS_PATH)
				.prettyPeek();
	}
	
	@Then("The data of {int} customers should be displayed")
	public void the_data_of_customers_should_be_displayed(Integer qtd) {
		response
			.then()
				.statusCode(HttpStatus.OK.value())
				.body(".", hasSize(qtd))
				.body("[0].id", is(1))
	            .body("[0].name", is("Sarah"))
	            .body("[0].email", is("sarah@mail.com"))
	            .body("[1].id", is(2))
	            .body("[1].name", is("Mike"))
				.body("[1].email", is("mike@mail.com"));

	}
	
	//Scenario: Getting a information about a customer
	@When("I get customer with ID {long}")
	public void get_product_with_id(Long id) {
		response = given(buildRequestSpecification()).contentType(ContentType.JSON)
				.pathParam("id", id)
				.when()
					.get(API_CUSTOMERS_PATH + "/{id}")
					.prettyPeek();
	}
	
	@Then("The data of customer with ID {long} should be displayed")
	public void data_of_customer_with_id_should_be_displayed(Long id) {
		response
			.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", is(1))
	            .body("name", is("Sarah"))
	            .body("email", is("sarah@mail.com"));
	}

	//Scenario: Trying to get a customer that not exists
	@When("I get customer with ID {long} that not exists")
	public void get_customer_with_id_that_not_exists(Long id) {
		response = given(buildRequestSpecification()).contentType(ContentType.JSON)
				.pathParam("id", id)
				.when()
					.get(API_CUSTOMERS_PATH+"/{id}")
					.prettyPeek();
	}
	
	//Scenario: Saving a customer
	@When("I save a new customer")
	public void save_new_customer() {
		CustomerInsertionDto request = new CustomerInsertionDto("Alice Brown", "alice@example.com");
		
		response = given(buildRequestSpecification())
					.contentType(ContentType.JSON)
					.body(request)
				.when()
					.post(API_CUSTOMERS_PATH)
					.prettyPeek();
	}
	@Then("The data of customer saved should be displayed")
	public void data_of_customer_saved_should_be_displayed() {
		response
			.then()
				.statusCode(HttpStatus.CREATED.value())
				.body("id", is(3))
	            .body("name", is("Alice Brown"))
	            .body("email", is("alice@example.com"));
	}
	
	//Scenario: Updating a customer
	@When("I update a customer with ID {long}")
	public void update_customer_with_id(Long id) {
		CustomerUpdateDto request = new CustomerUpdateDto(id, "Alice Brown", "alice@example.com");
		
		response = given(buildRequestSpecification())
					.contentType(ContentType.JSON)
					.body(request)
				.when()
					.put(API_CUSTOMERS_PATH)
					.prettyPeek();
	}
	@Then("The data of customer updated should be displayed")
	public void data_of_customer_updated_should_be_displayed() {
		response
			.then()
				.statusCode(HttpStatus.OK.value())
				.body("id", is(1))
	            .body("name", is("Alice Brown"))
	            .body("email", is("alice@example.com"));
	}
	
	//Scenario: Updating a customer that not exists
	@When("I update customer with ID {long} that not exists")
	public void update_customer_with_id_that_not_exists(Long id) {
		
		CustomerUpdateDto request = new CustomerUpdateDto(id, "Alice Brown", "alice@example.com");
		
		response = given(buildRequestSpecification())
					.contentType(ContentType.JSON)
					.body(request)
				.when()
					.put(API_CUSTOMERS_PATH)
					.prettyPeek();
	}

}
