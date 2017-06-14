user can issue a bill and finally pay the pill

Meta:

Narrative:
As a user
I want to issue a bill, accept it, have it guaranteed, received and paid
So that I can make use of the bill

GivenStories: stories/bill_issue_test.story

Scenario: a user accept the bill, have it guaranteed, received and pay the bill
Given bill in acceptance list of a
When I present acceptance of the bill to a as a
Then the bill is in the acceptance list of a
When I accept it as a
Then the new bill is accepted by a
When I register user judge
Then user judge is in the system
When I present guaranty of the bill to judge as a
Then the bill is in the guaranty list of judge
When I have the bill guaranteed by judge as a
Then the bill is guaranteed by judge
