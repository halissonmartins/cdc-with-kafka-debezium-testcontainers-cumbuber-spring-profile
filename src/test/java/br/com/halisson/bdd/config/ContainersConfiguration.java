package br.com.halisson.bdd.config;

import static org.assertj.core.api.Assertions.assertThat;
import static br.com.halisson.Constants.TZ_AMERICA_SAO_PAULO;

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

import br.com.halisson.Constants;
import io.debezium.testing.testcontainers.Connector.State;
import io.debezium.testing.testcontainers.ConnectorConfiguration;
import io.debezium.testing.testcontainers.DebeziumContainer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Testcontainers
public abstract class ContainersConfiguration {

	private static final String TABLE_NAME_CUSTOMERS = "testcontainers.customers";
	private static final String CONNECTOR_NAME = "my-connector";
	private static final String CONNECTOR_SINK_NAME = "jdbc-my-sink";

	protected static final String TOPIC_PREFIX = "from";

	private static final Network NETWORK = Network.newNetwork();

	@Container
	public static final PostgreSQLContainer<?> POSTGRES_SOURCE = new PostgreSQLContainer<>("postgres:18")
			.withNetwork(NETWORK)
			.withNetworkAliases("postgres-source")
			.withCopyFileToContainer(
					MountableFile.forClasspathResource(
							"./db/init_source.sql"),
							"/docker-entrypoint-initdb.d/init.sql")
			.withLogConsumer(new Slf4jLogConsumer(log).withPrefix("Postgres-Source"))
			.withDatabaseName("customer_source_db")
			.withCommand("postgres -c wal_level=logical")
			.withReuse(true)
			.withEnv("TZ", TZ_AMERICA_SAO_PAULO)
			.withEnv("PGTZ", TZ_AMERICA_SAO_PAULO);
	
	@Container
	public static final PostgreSQLContainer<?> POSTGRES_TARGET = new PostgreSQLContainer<>("postgres:18")
	.withNetwork(NETWORK)
	.withNetworkAliases("postgres-target")
	.withCopyFileToContainer(
			MountableFile.forClasspathResource(
					"./db/init_target.sql"),
			"/docker-entrypoint-initdb.d/init.sql")
	.withLogConsumer(new Slf4jLogConsumer(log).withPrefix("Postgres-Target"))
	.withDatabaseName("customer_target_db")
	.withReuse(true)
	.withEnv("TZ", TZ_AMERICA_SAO_PAULO)
	.withEnv("PGTZ", TZ_AMERICA_SAO_PAULO);

	@Container
	public static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.9.4"))
		.withNetwork(NETWORK)
		.withLogConsumer(new Slf4jLogConsumer(log).withPrefix("Kafka"))
		.withReuse(true);

	@Container
	public static final DebeziumContainer DEBEZIUM = new DebeziumContainer("quay.io/debezium/connect:3.3.1.Final")
			.withNetwork(NETWORK)
			.withKafka(KAFKA)
			.dependsOn(KAFKA, POSTGRES_SOURCE, POSTGRES_TARGET)
			.withLogConsumer(new Slf4jLogConsumer(log).withPrefix("Debezium"))
			.withReuse(true);

	static {

		log.info("\n============================" + "\n######## CREATING IMAGES" + "\n============================");

		// Start containers before testing
		Startables.deepStart(Stream.of(POSTGRES_SOURCE, POSTGRES_TARGET, KAFKA, DEBEZIUM)).join();	
		
		
		log.info("\n============================" + "\n######## CREATING CONNECTOR" + "\n============================");
		ConnectorConfiguration connector = ConnectorConfiguration.forJdbcContainer(POSTGRES_SOURCE)	
				.with("name", CONNECTOR_NAME)
        		.with("plugin.name", "pgoutput") //It is required when using Postgres container
        		.with("publication.name", "debezium_pub") //It is required when using Postgres container
        		.with("schema.include.list", "testcontainers")
        		.with("table.include.list", "testcontainers.customers")
        		.with("transforms", "unwrap")
        		.with("transforms.unwrap.type", "io.debezium.transforms.ExtractNewRecordState")
        		.with("value.converter", "org.apache.kafka.connect.json.JsonConverter")
        		.with("value.converter.schemas.enable", "true")
        		.with("table.include.list", TABLE_NAME_CUSTOMERS)
				.with("topic.prefix", TOPIC_PREFIX)
				.with("errors.log.enable", "true");
		
		log.info("\n============================" + "\n######## REGISTRING CONNECTOR" + "\n============================");
		DEBEZIUM.registerConnector(CONNECTOR_NAME, connector);	
		
		// Register JDBC Sink Connector
        ConnectorConfiguration jdbcSinkConfig = ConnectorConfiguration.create()
        	    .with("name", CONNECTOR_SINK_NAME)
        	    .with("connector.class", "io.debezium.connector.jdbc.JdbcSinkConnector")
        	    .with("tasks.max", "1")
        	    .with("topics", Constants.TO_TOPIC_NAME)
        	    .with("collection.name.format", TABLE_NAME_CUSTOMERS)
        	    .with("primary.key.mode", "record_value")
        	    .with("primary.key.fields", "id")
        	    .with("connection.url", POSTGRES_TARGET.getJdbcUrl()
        	    		.replace("localhost", "postgres-target")
        	    		.replace(POSTGRES_TARGET.getMappedPort(5432).toString(), "5432"))
        	    .with("connection.username", POSTGRES_TARGET.getUsername())
        	    .with("connection.password", POSTGRES_TARGET.getPassword())
        	    .with("insert.mode", "upsert")
        	    .with("value.converter", "org.apache.kafka.connect.json.JsonConverter")
        	    .with("value.converter.schemas.enable", "true")
        	    .with("transforms", "timestamp")
	    		.with("transforms.timestamp.type", "org.apache.kafka.connect.transforms.TimestampConverter$Value")
				.with("transforms.timestamp.target.type", "Timestamp")
				.with("transforms.timestamp.field", "updated_at")
				.with("transforms.timestamp.format", "yyyy-MM-dd HH:mm:ss.SSSXXX")
				.with("transforms.timestamp.timezone", "America/Sao_Paulo")
				.with("errors.log.enable", "true");

        log.info("\n============================" + "\n######## REGISTRING SINK CONNECTOR" + "\n============================");
        DEBEZIUM.registerConnector(CONNECTOR_SINK_NAME, jdbcSinkConfig);
		
		//Checking if Ok with connectors
		assertThat(DEBEZIUM.isConnectorConfigured(CONNECTOR_NAME)).isTrue();
		assertThat(DEBEZIUM.getConnectorState(CONNECTOR_NAME)).isEqualTo(State.RUNNING);
		assertThat(DEBEZIUM.isConnectorConfigured(CONNECTOR_SINK_NAME)).isTrue();
		assertThat(DEBEZIUM.getConnectorState(CONNECTOR_SINK_NAME)).isEqualTo(State.RUNNING);

	}	

    public static String securityProtocol() {
        return "PLAINTEXT";
    }

	@DynamicPropertySource
	public static void dynamicPropertySource(final DynamicPropertyRegistry registry) {
		log.info("\n============================" + "\n######## CONFIG STARTED" + "\n============================");

		// Postgres
        registry.add("spring.datasource.url", POSTGRES_SOURCE::getJdbcUrl);
        registry.add("spring.datasource.jdbc-url", POSTGRES_SOURCE::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_SOURCE::getUsername);
        registry.add("spring.datasource.password", POSTGRES_SOURCE::getPassword);
        
        // Kafka
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("kafka.security.protocol", ContainersConfiguration::securityProtocol);
	}

}
