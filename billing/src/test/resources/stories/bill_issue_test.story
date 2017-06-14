user can issue a bill

Meta:

Narrative:
As a user
I want to issue a bill
So that I can make use of the bill

Scenario: a user can issue a bill
Given system initialized with user a and b
When I list acceptance
Then no bill in the list
When I issue bill of price 1000 to b as a, due 2018-01-01 00:00:00
Then bill registered
When I list acceptance
Then the new bill is in the list
