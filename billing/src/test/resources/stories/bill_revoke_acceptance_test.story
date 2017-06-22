user can revoke unaccepted bills

Meta:

Narrative:
As a user
I want to revoke unaccepted bills
So that I can prevent loss

Scenario: a user can issue a bill
Given system initialized with user a and b
When I list acceptance of a
Then no bill in the list
When I issue bill of price 10 to b as a, due 60 seconds
Then bill registered
When I list acceptance of a
Then the new bill is in the list

Scenario: a user can revoke bills not accepted
Given bill in acceptance list of a
When I present revocation of the bill to a as a
And I list acceptance of a
Then no bill in the list
