@controller
Feature: Manage products
  As a user,
  I want to manage all produts
  So that I can  have the product data updated.

  @controller
  Scenario: Getting a information about all products
    Given I have 2 products included
    When I get all products
    Then The data of 2 products should be displayed

  @controller
  Scenario: Getting a information about a product
    Given I have 2 products included
    When I get product with ID 1
    Then The data of product with ID 1 should be displayed

  @controller
  Scenario: Trying to get a product that not exists
    Given I have a product with ID 999 that not exists
    When I get product with ID 999 that not exists
    Then Should be displayed a NotFound status

  @controller
  Scenario: Saving a product
    Given I have 2 products included
    When I save a new product
    Then The data of product saved should be displayed

  @controller
  Scenario: Updating a product
    Given I have 3 products included
    When I update a product with ID 1
    Then The data of product updated should be displayed

  @controller
  Scenario: Updating a product that not exists
    Given I have a product with ID 999 that not exists
    When I update product with ID 999 that not exists
    Then Should be displayed a NotFound status