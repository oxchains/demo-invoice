user can issue a bill and finally pay the pill

Meta:

Narrative:
As a user
I want to issue a bill, accept it, have it guaranteed, received and paid
So that I can make use of the bill

GivenStories: stories/bill_issue_test.story

Scenario: a user accept the bill, have it guaranteed, received and pay the bill
Given bill in endorsement list of b
When I present payment of the bill to a as b
Then the bill is in the payment list of a
When I have the bill paid by a as a
Then the bill is paid by a
