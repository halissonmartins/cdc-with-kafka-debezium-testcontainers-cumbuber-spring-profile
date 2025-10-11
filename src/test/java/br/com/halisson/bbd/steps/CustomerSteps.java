package br.com.halisson.bbd.steps;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import br.com.halisson.CustomerRepository;
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
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}
	
	@Given("I have a customer with ID {int} that not exists")
	public void have_customer_with_id_that_not_exists(Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
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
	public void the_data_of_customers_should_be_displayed(Integer int1) {
		response
			.then()
				.statusCode(200)
				.body(".", hasSize(2))
				.body("[0].id", is(1))
	            .body("[0].name", is("Sarah"))
	            .body("[0].email", is("sarah@mail.com"))
	            .body("[1].id", is(2))
	            .body("[1].name", is("Mike"))
				.body("[1].email", is("mike@mail.com"));

	}
	
	//Scenario: Getting a information about a customer
	@When("I get customer with ID {int}")
	public void get_product_with_id(Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}
	@Then("The data of customer with ID {int} should be displayed")
	public void data_of_customer_with_id_should_be_displayed(Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	//Scenario: Trying to get a customer that not exists
	@When("I get customer with ID {int} that not exists")
	public void get_customer_with_id_that_not_exists(Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}
	
	//Scenario: Saving a customer
	@When("I save a new customer")
	public void save_new_customer() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}
	@Then("The data of customer saved should be displayed")
	public void data_of_customer_saved_should_be_displayed() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}
	
	//Scenario: Updating a customer
	@When("I update a customer with ID {int}")
	public void update_customer_with_id(Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}
	@Then("The data of customer updated should be displayed")
	public void data_of_customer_updated_should_be_displayed() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}
	
	//Scenario: Updating a customer that not exists
	@When("I update customer with ID {int} that not exists")
	public void update_customer_with_id_that_not_exists(Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}


}
