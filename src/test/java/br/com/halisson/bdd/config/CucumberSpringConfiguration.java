package br.com.halisson.bdd.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext //TODO CHECK IF WORKS
@CucumberContextConfiguration
@Getter
public class CucumberSpringConfiguration extends ContainersConfiguration{
	
	protected static final String API_CUSTOMERS_PATH = "/customers";
	
	@LocalServerPort
	private Integer port;		
	
    protected RequestSpecification buildRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(getPort())
                .setBasePath("/api")
                .build();
    }
	
}
