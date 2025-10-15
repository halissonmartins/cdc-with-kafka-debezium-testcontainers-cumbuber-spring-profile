package br.com.halisson.bbd.steps;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.KafkaContainer;

import com.jayway.jsonpath.JsonPath;

import br.com.halisson.Customer;
import br.com.halisson.CustomerInsertionDto;
import br.com.halisson.CustomerRepository;
import br.com.halisson.CustomerUpdateDto;
import br.com.halisson.bdd.config.CucumberSpringConfiguration;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.debezium.testing.testcontainers.ConnectorConfiguration;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ReplicateCustomersSteps extends CucumberSpringConfiguration {	
	
	private static final UUID RANDOM_UUID = UUID.randomUUID();

	private static final String EMAIL_TO_UPDATE = "john@example.com";

	private final CustomerRepository customerRepository;

	private CustomerInsertionDto customerInsertionDto;
	
	private CustomerUpdateDto customerUpdateDto;
	
	private int expectedSize;
	private String expectedOperation;
	
	static {
		
		log.info("Running static ReplicateCustomersSteps");
		
		ConnectorConfiguration connector = ConnectorConfiguration.forJdbcContainer(POSTGRES_SOURCE)
				.with("topic.prefix", "dbserver1");
		
		DEBEZIUM.registerConnector("my-connector", connector);
		
	}

	//Common
	@Then("A replication event should be published to the message broker")
	public void a_replication_event_should_be_published_to_the_message_broker() {
		try (KafkaConsumer<String, String> consumer = getConsumer(KAFKA)) {

			consumer.subscribe(Arrays.asList("dbserver1.testcontainers.customers"));

			List<ConsumerRecord<String, String>> changeEvents = drain(consumer, expectedSize);

			//Getting the index of last registry inserted
			int index = changeEvents.size() - 1;
			
			log.info("EventJpa0: {}", changeEvents.get(index));
			assertThat(JsonPath.<Integer>read(changeEvents.get(index).key(), "$.id")).isEqualTo(4);
			assertThat(JsonPath.<String>read(changeEvents.get(index).value(), "$.op")).isEqualTo(expectedOperation);
			
			String name = null;
			String email = null;
			
			if(expectedOperation.equals("r")) {
				name = customerInsertionDto.name();
				email = customerInsertionDto.email();
			}
			
			if(expectedOperation.equals("u")) {
				name = customerUpdateDto.name();
				email = customerUpdateDto.email();
			}
			
			assertThat(JsonPath.<String>read(changeEvents.get(index).value(), "$.after.name")).isEqualTo(name);
			assertThat(JsonPath.<String>read(changeEvents.get(index).value(), "$.after.email")).isEqualTo(email);
			
			consumer.unsubscribe();
		}
	}
	
	//Scenario: Saving a customer to replicate
	@Given("new customer with name {string} and email {string}")
	public void new_customer_with_name_and_email(String name, String email) {
	    
		customerInsertionDto = new CustomerInsertionDto(name, email);
		
		assertThat(customerInsertionDto.name()).isEqualTo(name);
		assertThat(customerInsertionDto.email()).isEqualTo(email);
	}
	
	@When("I save this new customer to replicate")
	public void save_this_new_customer_to_replicate() {
		
		given(buildRequestSpecification())
					.contentType(ContentType.JSON)
					.body(customerInsertionDto)
				.when()
					.post(API_CUSTOMERS_PATH)
					.prettyPeek()
				.then()
					.statusCode(HttpStatus.CREATED.value())
					.body("id", is(4))
		            .body("name", is(customerInsertionDto.name()))
		            .body("email", is(customerInsertionDto.email()));
		
		expectedSize = 4;
		expectedOperation = "r";
	}
	
	@And("The customer with ID {long} should be persisted in the database")
	public void the_customer_with_id_should_be_persisted_in_the_database(Long id) {
		
		Optional<Customer> optionalCustomer = customerRepository.findById(id);
		assertTrue(optionalCustomer.isPresent());
	}

	@And("The data of customer saved should be replicated")
	public void data_of_customer_saved_should_be_replicated() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}
	
	//Scenario: Updating a customer to replicate
	@Given("I have a customer with ID {long}")
	public void have_a_customer_with_id(Long id) {
		
		Optional<Customer> optionalCustomer = customerRepository.findById(id);
		assertTrue(optionalCustomer.isPresent());
		
		String name = optionalCustomer.get().getName();
		
		customerUpdateDto = new CustomerUpdateDto(id, name, EMAIL_TO_UPDATE);
		
		assertThat(customerUpdateDto.id()).isEqualTo(id);
		assertThat(customerUpdateDto.name()).isEqualTo(name);
		assertThat(customerUpdateDto.email()).isEqualTo(EMAIL_TO_UPDATE);
		
	}
	
	@When("I update the email of customer to replicate")
	public void update_the_email_of_customer_to_replicate() {
		
		given(buildRequestSpecification())
					.contentType(ContentType.JSON)
					.body(customerUpdateDto)
				.when()
					.put(API_CUSTOMERS_PATH)
					.prettyPeek()
				.then()
					.statusCode(HttpStatus.OK.value())
					.body("id", is(customerUpdateDto.id().intValue()))
		            .body("name", is(customerUpdateDto.name()))
		            .body("email", is(customerUpdateDto.email()));
		
		expectedSize = 1;
		expectedOperation = "u";
	}
	
	@And("The customer should be updated in the database")
	public void the_customer_should_be_updated_in_the_database() {
		
		Optional<Customer> optionalCustomer = customerRepository.findById(customerUpdateDto.id());
		assertTrue(optionalCustomer.isPresent());
		assertThat(optionalCustomer.get().getEmail()).isEqualTo(EMAIL_TO_UPDATE);
	}

	@And("The data of customer updated should be replicated")
	public void data_of_customer_updated_should_be_replicated() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}
	


	// Helper methods below

	private KafkaConsumer<String, String> getConsumer(KafkaContainer kafkaContainer) {

		return new KafkaConsumer<>(
				Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers(),
						ConsumerConfig.GROUP_ID_CONFIG, "tc-" + RANDOM_UUID,
						ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"),
				new StringDeserializer(), new StringDeserializer());
	}

	private List<ConsumerRecord<String, String>> drain(KafkaConsumer<String, String> consumer,
			int expectedRecordCount) {

		List<ConsumerRecord<String, String>> allRecords = new ArrayList<>();

		Unreliables.retryUntilTrue(10, TimeUnit.SECONDS, () -> {
			consumer.poll(Duration.ofMillis(50)).iterator().forEachRemaining(allRecords::add);
			
			log.info("allRecordsDrained {}", allRecords.size());

			return allRecords.size() == expectedRecordCount;
		});

		return allRecords;
	}
}
