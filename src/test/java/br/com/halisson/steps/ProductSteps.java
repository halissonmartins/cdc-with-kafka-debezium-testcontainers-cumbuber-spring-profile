package br.com.halisson.steps;

import br.com.halisson.config.CucumberSpringConfiguration;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ProductSteps extends CucumberSpringConfiguration{

	//Commons
	@Given("I have {int} products included")
	public void have_products_included(Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    //throw new io.cucumber.java.PendingException();
	}	
	@Then("Should be displayed a NotFound status")
	public void should_be_displayed_a_not_found_status() {
	    // Write code here that turns the phrase above into concrete actions
	    //throw new io.cucumber.java.PendingException();
	}
	@Given("I have a product with ID {int} that not exists")
	public void have_product_with_id_that_not_exists(Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    //throw new io.cucumber.java.PendingException();
	}
	

	//Scenario: Getting a information about all products	
	@When("I get all products")
	public void get_all_products() {
	    // Write code here that turns the phrase above into concrete actions
	    //throw new io.cucumber.java.PendingException();
	}
	@Then("The data of {int} products should be displayed")
	public void the_data_of_products_should_be_displayed(Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    //throw new io.cucumber.java.PendingException();
	}
	
	//Scenario: Getting a information about a product
	@When("I get product with ID {int}")
	public void get_product_with_id(Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    //throw new io.cucumber.java.PendingException();
	}
	@Then("The data of product with ID {int} should be displayed")
	public void data_of_product_with_id_should_be_displayed(Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    //throw new io.cucumber.java.PendingException();
	}

	//Scenario: Trying to get a product that not exists
	@When("I get product with ID {int} that not exists")
	public void get_product_with_id_that_not_exists(Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    //throw new io.cucumber.java.PendingException();
	}
	
	//Scenario: Saving a product
	@When("I save a new product")
	public void save_new_product() {
	    // Write code here that turns the phrase above into concrete actions
	    //throw new io.cucumber.java.PendingException();
	}
	@Then("The data of product saved should be displayed")
	public void data_of_product_saved_should_be_displayed() {
	    // Write code here that turns the phrase above into concrete actions
	    //throw new io.cucumber.java.PendingException();
	}
	
	//Scenario: Updating a product
	@When("I update a product with ID {int}")
	public void update_product_with_id(Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    //throw new io.cucumber.java.PendingException();
	}
	@Then("The data of product updated should be displayed")
	public void data_of_product_updated_should_be_displayed() {
	    // Write code here that turns the phrase above into concrete actions
	    //throw new io.cucumber.java.PendingException();
	}
	
	//Scenario: Updating a product that not exists
	@When("I update product with ID {int} that not exists")
	public void update_product_with_id_that_not_exists(Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    //throw new io.cucumber.java.PendingException();
	}

}
