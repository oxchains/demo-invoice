the bill can be endorsed by the payee

Meta:

Narrative:
As a payee
I want to endorse the bill to others
So that others can recourse the bill

GivenStories: stories/bill_issue_test.story

Scenario: a user can endorse the bill to others
Given bill in endorsement list of b
When I register user endorsee with balance 1000
Then user endorsee is in the system
When I present endorsement of the bill to endorsee as b
Then the bill is in the endorsement list of endorsee
When I have the bill endorsed by b as endorsee
Then the bill is in the endorsement list of endorsee
When I present payment of the bill to a as endorsee
Then the bill is in the payment list of a
And the bill is in the payment list of endorsee
When I have the bill revoked as a
Then the bill is in the recourse list of endorsee
When I present recourse of the bill to warrantor as endorsee
Then the bill is in the payment list of warrantor
When I have the bill paid by warrantor as warrantor
Then the bill is paid by warrantor
