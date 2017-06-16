payer can revoke unguaranteed bills

Meta:

Narrative:
As a payer
I want to revoke unguaranteed bills
So that I can prevent loss

Scenario: a user can issue a bill
Given system initialized with user a and b
When I list acceptance of a
Then no bill in the list
When I issue bill of price 10 to b as a, due 60 seconds
Then bill registered
When I list acceptance of a
Then the new bill is in the list

Scenario: a user can revoke bills not guaranteed
Given bill in acceptance list of a
When I present acceptance of the bill to a as a
Then the bill is in the acceptance list of a
When I accept it as a
Then the new bill is accepted by a
And the bill is in the guaranty list of a
When I present revocation of the bill to a as a
And I list guaranty of a
Then no bill in the list
