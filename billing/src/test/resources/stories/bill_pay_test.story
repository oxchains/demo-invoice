the bill can be paid by the payer

Meta:

Narrative:
As a payee
I want to present the bill to payer
So that the payer can pay the bill

GivenStories: stories/bill_issue_test.story

Scenario: a user can promot the payer to pay the bill
Given bill in endorsement list of b
When I present payment of the bill to a as b
Then the bill is in the payment list of a
When I have the bill paid by a as a
Then the bill is paid by a
