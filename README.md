# CDC with Kafka, Debezium, TestContainers, Cumbuber, Spring and Maven Profile

## Description
This project demonstrates how to implement Change Data Capture (CDC) using Apache Kafka and Debezium. It leverages Testcontainers to provide reliable and isolated integration testing environments, Cucumber for behavior-driven development (BDD) testing, Spring Boot for streamlined application configuration and Maven Profiles for flexible build management. The repository serves as a practical reference for building, testing, and running robust CDC solutions. The repository serves as a reference architecture for building robust, testable, and maintainable CDC pipelines in Java-based applications.

**Key Features:**
- **Change Data Capture Integration:** Monitors database changes in real-time using Debezium and streams them to Kafka topics.
- **Testcontainers Support:** Provides easy-to-use, containerized testing environments for Kafka, databases, and other dependencies.
- **Cucumber BDD Tests:** Ensures business requirements are met through behavior-driven development.
- **Spring Boot:** Simplifies configuration for different environments (development, testing, production).
- **Maven Profile Management:** Enables build-time configuration and customization for various environments or use cases.
- **Example Workflows:** Includes sample scenarios and test cases to help you get started quickly.

This repository is ideal for developers looking to explore or implement CDC solutions in distributed systems, and for teams aiming to enhance their testing strategies with modern Java-based tools.

## Programs that need to be installed and started beforehand
- Docker
- JDK 25
- MAVEN

## Instructions
- Set JAVA_HOME with JDK 25
- Just run in terminal without Cucumber BDD Tests
	```
	mvn clean install
	```
	
- Just run in terminal with Cucumber BDD Tests
	```
	mvn clean install -P bdd-tests
	```
	
- Just run in terminal with all Tests
	```
	mvn clean install -P all-tests
	```

## Steps to run containers via Docker
- In the terminal, execute the following command:
  ```sh
  docker compose -f docker-compose.yml up -d
  ```

- Wait for all containers to initialize.
- In the terminal, execute the command to stop all containers.
  ```sh
  docker compose -f docker-compose.yml down --volumes
  ```  

> Note: The project contains a collection with all REST requests for importing into Postman or Insomnia.

## References

### 1. Testcontainers
- https://docs.spring.io/spring-boot/reference/testing/testcontainers.html
- https://medium.com/@AlexanderObregon/running-integration-tests-in-spring-boot-with-testcontainers-0011b97249d9
- https://java.testcontainers.org/modules/docker_compose/
- https://testcontainers.com/guides/testing-spring-boot-rest-api-using-testcontainers/
- https://www.baeldung.com/spring-boot-testcontainers-integration-test
- https://www.geeksforgeeks.org/advance-java/built-in-testcontainers-support-in-spring-boot/
- https://www.freecodecamp.org/portuguese/news/como-realizar-testes-de-integracao-usando-junit-5-e-testcontainers-com-springboot/

### 2. Flyway
- https://medium.com/javarevisited/spring-boot-testing-testcontainers-and-flyway-df4a71376db4
- https://testcontainers.com/guides/working-with-jooq-flyway-using-testcontainers/

### 3. REST Assured
- https://www.baeldung.com/rest-assured-response
- https://naodeng.medium.com/rest-assured-tutorial-advanced-usage-validating-responses-and-logging-filters-file-uploads-65ba7e6ab080
- https://medium.com/@m4manishd/mastering-response-handling-with-rest-assured-in-automation-testing-72872696da96

### 4. Kafka
- https://testcontainers.com/guides/testing-spring-boot-kafka-listener-using-testcontainers/
- https://www.baeldung.com/spring-boot-kafka-testing
- https://medium.com/@uilenlelles/testcontainers-com-springboot-cucumber-junit-5-kafka-d455d18f9d4d
- https://dev.to/rafaelfantinel/testando-kafka-no-spring-boot-com-testcontainers-3hoe
- https://www.geekyhacker.com/write-kafka-integration-test-with-testcontainers/

### 5. Debezium
- https://debezium.io/documentation/reference/stable/integrations/testcontainers.html
- https://stackoverflow.com/questions/77128033/testing-custom-kafka-connect-smt-using-test-containers
- https://github.com/bobmarks/kafka-connect-testcontainers-custom-smt
- https://github.com/debezium/debezium-connector-jdbc/

### 6. Spring Profiles
- https://medium.com/@AlexanderObregon/mechanics-of-spring-boot-profiles-and-configurations-be81fac082f5
- https://www.geeksforgeeks.org/advance-java/configuring-spring-boot-applications-with-maven-profiles/
- https://medium.com/@bectorhimanshu/spring-boot-integration-testing-for-dynamodb-using-testcontainers-localstack-testresttemplate-dd4f4469eb87
- https://medium.com/@bectorhimanshu/mastering-spring-profiles-a-guide-to-environment-specific-configurations-82879501fb19
- https://www.baeldung.com/spring-boot-junit-5-testing-active-profile
- https://medium.com/@AlexanderObregon/using-profiles-to-separate-dev-and-test-beans-in-spring-boot-0dea61d59caa
- https://docs.spring.io/spring-boot/reference/features/profiles.html#features.profiles

### 7. Cucumber
- https://medium.com/@uilenlelles/integra%C3%A7%C3%A3o-springboot-cucumber-junit-5-328739384d03
- https://medium.com/@uilenlelles/testcontainers-com-springboot-cucumber-junit-5-kafka-d455d18f9d4d
- https://medium.com/@uilenlelles/testcontainers-com-springboot-cucumber-junit-5-rabbitmq-814d3ee48f38
- https://medium.com/@uilenlelles/testcontainers-com-springboot-cucumber-junit-5-oracledatabase-6ed5d081f0bc
- https://medium.com/@jignect/mastering-bdd-with-cucumber-and-java-advanced-techniques-for-scalable-test-automation-5ac447746f0f

## TODO
- [X] Debezium Connect container, Connector and Sink
- [ ] In test source connector use same properties that from_source.json
- [X] Fix problem that not replicate when running the application
- [X] Kafka ingestion with add timestamp column
- [ ] Fix problem with time zone in timestamp column
- [X] Use Confluent Kafka
- [ ] Use Confluent Connect container, Connector and Sink
- [ ] Use MS SQL as source database
- [ ] Use DB2 as target database
- [X] Fix problem when build with GitHub Actions
- [ ] Add playground projects
- [ ] Configure to delete all containers when application stop (integration Spring and Compose)



- Scenario Outline?
