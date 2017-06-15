the bill can be pledged by the payee

Meta:

Narrative:
As a payee
I want to pledge the bill to others
So that others can recourse the bill

GivenStories: stories/bill_issue_test.story

Scenario: a user accept the bill, have it guaranteed, received and pay the bill
Given bill in endorsement list of b
When I register user pledgee with balance 1000
Then user pledgee is in the system
When I present pledge of the bill to pledgee as b
Then the bill is in the pledge list of pledgee
When I have the bill pledged for b as pledgee
Then the bill is in the pledge/release list of pledgee
When I present pledge/release of the bill to b as pledgee
Then the bill is in the pledge/release list of b
When I have the bill pledge released by pledgee as b
Then the bill is in the endorsement list of b
