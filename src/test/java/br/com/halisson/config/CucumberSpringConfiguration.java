package br.com.halisson.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import io.cucumber.spring.CucumberContextConfiguration;

//@ActiveProfiles("bdd")
//@SpringBootTest
//@EnabledIf(expression = "#{environment.acceptsProfiles('bdd')}", loadContext = false)
@CucumberContextConfiguration
public class CucumberSpringConfiguration {

}
