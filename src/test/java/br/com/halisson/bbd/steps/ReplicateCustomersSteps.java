package br.com.halisson.bbd.steps;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static br.com.halisson.Constants.TZ_AMERICA_SAO_PAULO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
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
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ReplicateCustomersSteps extends CucumberSpringConfiguration {	

	private static final String QUERIE = "SELECT * FROM testcontainers.customers c WHERE c.id = %d";

	private static final UUID RANDOM_UUID = UUID.randomUUID();

	private static final String EMAIL_TO_UPDATE = "john@example.com";

	private final CustomerRepository customerRepository;

	private CustomerInsertionDto customerInsertionDto;
	
	private CustomerUpdateDto customerUpdateDto;
	
	private String expectedOperation;

	//Common
	@Then("A replication event should be published to the message broker")
	public void replication_event_should_be_published_to_the_message_broker() {
		try (KafkaConsumer<String, String> consumer = getConsumer(KAFKA)) {

			consumer.subscribe(Arrays.asList(TOPIC_PREFIX+".testcontainers.customers"));

			List<ConsumerRecord<String, String>> changeEvents = drain(consumer, 1);

			//Getting the index of last registry inserted
			int index = changeEvents.size() - 1;
			
			log.info("EventJpa{}: {}", index, changeEvents.get(index).value());
			
			String name = null;
			String email = null;
			
			if(expectedOperation.equals("c")) {
				name = customerInsertionDto.name();
				email = customerInsertionDto.email();
			}else if(expectedOperation.equals("u")) {
				name = customerUpdateDto.name();
				email = customerUpdateDto.email();
			}else {
				throw new IllegalArgumentException("Operation not expected.");
			}
			
			assertThat(JsonPath.<Integer>read(changeEvents.get(index).value(), "$.payload.id")).isEqualTo(4);
			assertThat(JsonPath.<String>read(changeEvents.get(index).value(), "$.payload.name")).isEqualTo(name);
			assertThat(JsonPath.<String>read(changeEvents.get(index).value(), "$.payload.email")).isEqualTo(email);
			
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
		
		//Create Operation
		expectedOperation = "c";
	}
	
	@And("The customer with ID {long} should be persisted in the database")
	public void the_customer_with_id_should_be_persisted_in_the_database(Long id) {
		
		Optional<Customer> optionalCustomer = customerRepository.findById(id);
		assertTrue(optionalCustomer.isPresent());
	}

	@And("The data of customer saved should be replicated")
	public void data_of_customer_saved_should_be_replicated() throws Exception{

        log.info("\n============================" + "\n######## WAITING SINK PROCESS" + "\n============================");
        Thread.sleep(1000); // wait for sink process finish     
        
        //log.info("\n============================" + "\n######## DEBEZIUM LOGS - SAVE" + "\n============================");
        //log.info(DEBEZIUM.getLogs());
        
        log.info("\n============================" + "\n######## CHECKING INTO POSTGRES" + "\n============================");
        try (Connection conn = DriverManager.getConnection(
                POSTGRES_TARGET.getJdbcUrl(), POSTGRES_TARGET.getUsername(), POSTGRES_TARGET.getPassword())) {
        	
			ResultSet rs = conn.createStatement().executeQuery(String.format(QUERIE, 4));
			
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo(customerInsertionDto.name());
            assertThat(rs.getString("email")).isEqualTo(customerInsertionDto.email());           
            assertThat(rs.getTimestamp("updated_at", 
            		Calendar.getInstance(TimeZone.getTimeZone(TZ_AMERICA_SAO_PAULO)))).isNotNull();
            
            LocalDateTime now = LocalDateTime.now(ZoneId.of(TZ_AMERICA_SAO_PAULO));
            log.info("NowDateTZ: {}", now.toString());         
            
			long nowMillis = now.atZone(ZoneId.of(TZ_AMERICA_SAO_PAULO))
					.toInstant().toEpochMilli();
            
            assertThat(rs.getTimestamp("updated_at", Calendar.getInstance(TimeZone.getTimeZone(TZ_AMERICA_SAO_PAULO))))
            	.isBefore(new java.sql.Timestamp(nowMillis));
        }
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
		
		//Update Operation
		expectedOperation = "u";
	}
	
	@And("The customer should be updated in the database")
	public void the_customer_should_be_updated_in_the_database() {
		
		Optional<Customer> optionalCustomer = customerRepository.findById(customerUpdateDto.id());
		assertTrue(optionalCustomer.isPresent());
		assertThat(optionalCustomer.get().getEmail()).isEqualTo(EMAIL_TO_UPDATE);
	}

	@And("The data of customer updated should be replicated")
	public void data_of_customer_updated_should_be_replicated() throws Exception{

        log.info("\n============================" + "\n######## WAITING SINK PROCESS" + "\n============================");
        Thread.sleep(1000); // wait for sink process finish 
        
        //log.info("\n============================" + "\n######## DEBEZIUM LOGS - UPDATE" + "\n============================");
        //log.info(DEBEZIUM.getLogs());        
        
        log.info("\n============================" + "\n######## CHECKING INTO POSTGRES" + "\n============================");
        try (Connection conn = DriverManager.getConnection(
                POSTGRES_TARGET.getJdbcUrl(), POSTGRES_TARGET.getUsername(), POSTGRES_TARGET.getPassword())) {
        	
			ResultSet rs = conn.createStatement().executeQuery(String.format(QUERIE, customerUpdateDto.id()));
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("email")).isEqualTo(EMAIL_TO_UPDATE);			
			assertThat(rs.getTimestamp("updated_at")).isNotNull();			
            
            LocalDateTime now = LocalDateTime.now(ZoneId.of(TZ_AMERICA_SAO_PAULO));
            log.info("NowDateTZ: {}", now.toString());
			long nowMillis = now.atZone(ZoneId.of(TZ_AMERICA_SAO_PAULO))
					.toInstant().toEpochMilli();
            
            assertThat(rs.getTimestamp("updated_at", Calendar.getInstance(TimeZone.getTimeZone(TZ_AMERICA_SAO_PAULO))))
            	.isBefore(new java.sql.Timestamp(nowMillis));
        }
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

		Unreliables.retryUntilTrue(1, TimeUnit.SECONDS, () -> {
			consumer.poll(Duration.ofMillis(50)).iterator().forEachRemaining(allRecords::add);
			
			log.info("allRecordsDrained {}", allRecords.size());

			return allRecords.size() == expectedRecordCount;
		});

		return allRecords;
	}
}
