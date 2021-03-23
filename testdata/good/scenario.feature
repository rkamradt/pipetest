Feature: Creating and retrieving a purchase order

  Scenario: happy path
    Given a new purchase order
    When you post the new purchase order
    Then you should get an id
    And you should be able to get the purchase order by id
    And it should be the same as the original purchase order

