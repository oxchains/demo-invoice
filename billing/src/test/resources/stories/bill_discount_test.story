the bill can be discounted by the bank

Meta:

Narrative:
As a payee
I want to pledge the bill to others
So that others can recourse the bill

GivenStories: stories/bill_issue_test.story

Scenario: a user can ask the bank to discount the bill
Given bill in endorsement list of b
When I register user bank with balance 9999999
Then user bank is in the system
When I present discount of the bill to bank as b
Then the bill is in the discount list of bank
When I have the bill discounted for b as bank
Then the bill is in the endorsement list of bank
When I present payment of the bill to a as bank
Then the bill is in the payment list of a
When I have the bill paid by a as a
Then the bill is paid by a
