@controller
Feature: Manage customers
  As a user,
  I want to manage all customers
  So that I can  have the customer data updated.

  @controller
  Scenario: Getting a information about all customers
    Given I have 2 customers included
    When I get all customers
    Then The data of 2 customers should be displayed

  @controller
  Scenario: Getting a information about a customer
    Given I have 2 customers included
    When I get customer with ID 1
    Then The data of customer with ID 1 should be displayed

  @controller
  Scenario: Trying to get a customer that not exists
    Given I have a customer with ID 999 that not exists
    When I get customer with ID 999 that not exists
    Then Should be displayed a NotFound status

  @controller
  Scenario: Saving a customer
    Given I have 2 customers included
    When I save a new customer
    Then The data of customer saved should be displayed

  @controller
  Scenario: Updating a customer
    Given I have 3 customers included
    When I update a customer with ID 1
    Then The data of customer updated should be displayed

  @controller
  Scenario: Updating a customer that not exists
    Given I have a customer with ID 999 that not exists
    When I update customer with ID 999 that not exists
    Then Should be displayed a NotFound status