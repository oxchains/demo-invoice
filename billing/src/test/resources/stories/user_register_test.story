system can register user

Meta:

Narrative:
As a system user
I want to register a user
So that I can make use of him in the bill exchange process

Scenario: system can register a user
Given system initialized with user a and b
When I register user b with balance 1000
Then registration fail
And a new user is not in the system
When I register a new user
Then the new user is in the system
