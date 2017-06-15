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
When I issue bill of price 10 to b as a, due 60 seconds
Then bill registered
When I list acceptance
Then the new bill is in the list

Scenario: a user can receive a bill
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
When I present reception of the bill to b as a
Then the bill is in the reception list of a
When I have the bill received by b as b
