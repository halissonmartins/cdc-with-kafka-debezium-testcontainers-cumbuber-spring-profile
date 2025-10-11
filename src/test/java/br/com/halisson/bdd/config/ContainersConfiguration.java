package br.com.halisson.bdd.config;

import java.util.stream.Stream;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Testcontainers
public abstract class ContainersConfiguration {

	private static final Network NETWORK = Network.newNetwork();

	public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
			DockerImageName.parse("quay.io/debezium/postgres:18").asCompatibleSubstituteFor("postgres"))
			.withNetwork(NETWORK).withNetworkAliases("postgres")
			.withCopyFileToContainer(
					MountableFile.forClasspathResource(
							"./db/init.sql"),
							"/docker-entrypoint-initdb.d/init.sql")
			.withLogConsumer(new Slf4jLogConsumer(log))
			.withCommand("postgres -c wal_level=logical")
			.withReuse(true);

	static {

		log.info("\n============================" + "\n######## CREATING IMAGES" + "\n============================");

		// Start containers before testing
		Startables.deepStart(Stream.of(POSTGRES)).join();

	}

	@DynamicPropertySource
	public static void dynamicPropertySource(final DynamicPropertyRegistry registry) {
		log.info("\n============================" + "\n######## CONFIG STARTED" + "\n============================");

		// Postgres
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
	}
}
