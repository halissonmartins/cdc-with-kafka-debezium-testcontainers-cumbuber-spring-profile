package br.com.halisson.bbd.steps;
import static org.junit.jupiter.api.Assertions.assertEquals;

import br.com.halisson.bdd.config.CucumberSpringConfiguration;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleExampleStep extends CucumberSpringConfiguration{
	
    private String testString;
    private String resultString;

    @Given("I have a string with value {string}")
    public void i_have_a_string_with_value(String string) {
    	log.info("GIVEN: ");
        testString = string;
    }

    @When("I reverse the string")
    public void i_reverse_the_string() {
    	log.info("WHEN: ");
        resultString = new StringBuilder(testString).reverse().toString();
    }

    @Then("the result should be {string}")
    public void the_result_should_be(String expectedResult) {
    	log.info("THEN: ");
        assertEquals(expectedResult, resultString);
    }
}
