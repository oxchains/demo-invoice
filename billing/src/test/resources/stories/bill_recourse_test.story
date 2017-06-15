the bill can be paid by the warrant

Meta:

Narrative:
As a payee
I want to recourse the bill to warrant
So that the warrant can pay the bill

GivenStories: stories/bill_issue_test.story

Scenario: a user accept the bill, have it guaranteed, received and pay the bill
Given bill in endorsement list of b
When I present payment of the bill to a as b
Then the bill is in the payment list of a
When I have the bill revoked as a
Then the bill is in the recourse list of b
When I present recourse of the bill to warrantor as b
Then the bill is in the payment list of warrantor
When I have the bill paid by warrantor as warrantor
Then the bill is paid by warrantor
