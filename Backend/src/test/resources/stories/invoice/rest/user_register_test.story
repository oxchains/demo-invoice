User Register

Narrative:
As a user
I want to register
So that I can make use of the system

Scenario: a user can register with sufficient information
Meta: @id1 JD
Given name JD, mobile 13800138000, password testpass
When I register user
Then user registration success
When I enroll user
Then user JD enrolled

Scenario: a user can register with sufficient information
Meta: @id1 oxchains
Given name oxchains, mobile 13800138000, password testpass
When I register user
Then user registration success
When I enroll user
Then user oxchains enrolled
