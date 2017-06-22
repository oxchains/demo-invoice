payer can only revoke unreceived bills, not received bills

Meta:

Narrative:
As a payer
I want to revoke unreceived bills
So that I can prevent loss

Scenario: a user can issue a bill
Given system initialized with user a and b
When I list acceptance of a
Then no bill in the list
When I issue bill of price 10 to b as a, due 60 seconds
Then bill registered
When I list acceptance of a
Then the new bill is in the list

Scenario: a user can revoke bills not received
Given bill in acceptance list of a
When I present acceptance of the bill to a as a
Then the bill is in the acceptance list of a
When I accept it as a
Then the new bill is accepted by a
When I register user warrantor with balance 1000
Then user warrantor is in the system
When I present guaranty of the bill to warrantor as a
Then the bill is in the guaranty list of warrantor
When I have the bill guaranteed by warrantor as a
Then the bill is guaranteed by warrantor
And the bill is in the reception list of a
When I present revocation of the bill to a as a
And I list reception of a
Then no bill in the list

Scenario: a user can issue a bill
Given system initialized with user a and b
When I list acceptance of a
Then no bill in the list
When I issue bill of price 10 to b as a, due 60 seconds
Then bill registered
When I list acceptance of a
Then the new bill is in the list

Scenario: a user cannot revoke received bills
Given bill in acceptance list of a
When I present acceptance of the bill to a as a
Then the bill is in the acceptance list of a
When I accept it as a
Then the new bill is accepted by a
When I register user warrantor with balance 1000
Then user warrantor is in the system
When I present guaranty of the bill to warrantor as a
Then the bill is in the guaranty list of warrantor
When I have the bill guaranteed by warrantor as a
Then the bill is guaranteed by warrantor
When I present reception of the bill to b as a
Then the bill is in the reception list of a
When I have the bill received by b as b
Then the bill is received by b
And the bill is in the endorsement list of b
When I present revocation of the bill to a as a
Then the bill is in the endorsement list of b
