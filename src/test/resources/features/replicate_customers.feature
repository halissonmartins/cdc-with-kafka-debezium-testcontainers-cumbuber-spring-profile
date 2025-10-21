@cdc
Feature: Replicate customers
  As a system,
  I want to replicate all customers to target database
  So that I can have the customer data syncronized with target database.

  @cdc
  Scenario: Saving a customer to replicate
    Given new customer with name "John Doe" and email "john.doe@example.com"
    When I save this new customer to replicate
    Then The customer with ID 4 should be persisted in the database
		And A replication event should be published to the message broker
    And The data of customer saved should be replicated

  @cdc
  Scenario: Updating a customer to replicate
    Given I have a customer with ID 4
    When I update the email of customer to replicate
    Then The customer should be updated in the database
		And A replication event should be published to the message broker
    And The data of customer updated should be replicated