User Register

Narrative:
As a user
I want to register
So that I can make use of the system

Scenario: a user can register with sufficient information
Given name Jack Ma, mobile 13800138000, password testpass
When I register user
Then user registration success
