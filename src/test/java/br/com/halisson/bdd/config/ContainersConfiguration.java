package br.com.halisson.bdd.config;

import java.util.stream.Stream;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import io.debezium.testing.testcontainers.DebeziumContainer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Testcontainers
public abstract class ContainersConfiguration {

	private static final Network NETWORK = Network.newNetwork();

	@Container
	public static final PostgreSQLContainer<?> POSTGRES_SOURCE = new PostgreSQLContainer<>(
			DockerImageName.parse("quay.io/debezium/postgres:18").asCompatibleSubstituteFor("postgres"))
			.withNetwork(NETWORK).withNetworkAliases("postgres")
			.withCopyFileToContainer(
					MountableFile.forClasspathResource(
							"./db/init_source.sql"),
							"/docker-entrypoint-initdb.d/init.sql")
			.withLogConsumer(new Slf4jLogConsumer(log))
			//TODO CHECK TOPIC NAME WHEN CHANGE DATABASE NAME
			//.withDatabaseName("customer_source_db")
			.withCommand("postgres -c wal_level=logical")
			.withReuse(true);

	@Container
	public static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.9.4"))
		.withNetwork(NETWORK)
		.withLogConsumer(new Slf4jLogConsumer(log))
		.withReuse(true);

	@Container
	public static final DebeziumContainer DEBEZIUM = new DebeziumContainer("quay.io/debezium/connect:3.2.4.Final")
			.withNetwork(NETWORK)
			.withKafka(KAFKA)
			.dependsOn(KAFKA)
			.withLogConsumer(new Slf4jLogConsumer(log))
			.withReuse(true);

	static {

		log.info("\n============================" + "\n######## CREATING IMAGES" + "\n============================");

		// Start containers before testing
		Startables.deepStart(Stream.of(POSTGRES_SOURCE, KAFKA, DEBEZIUM)).join();

	}	

    public static String securityProtocol() {
        return "PLAINTEXT";
    }

	@DynamicPropertySource
	public static void dynamicPropertySource(final DynamicPropertyRegistry registry) {
		log.info("\n============================" + "\n######## CONFIG STARTED" + "\n============================");

		// Postgres
        registry.add("spring.datasource.url", POSTGRES_SOURCE::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_SOURCE::getUsername);
        registry.add("spring.datasource.password", POSTGRES_SOURCE::getPassword);
        
        // Kafka
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("kafka.security.protocol", ContainersConfiguration::securityProtocol);
	}
}
