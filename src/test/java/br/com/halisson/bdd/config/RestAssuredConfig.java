package br.com.halisson.bdd.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

//TODO NOT USED
@TestConfiguration
public class RestAssuredConfig {
	
	@LocalServerPort
	private Integer port;

    @Bean
    RequestSpecification requestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(port)
                .setBasePath("/api")
                .build();
    }
}

