Company Register

Meta:

Narrative:
As a company user
I want to register
So that I can make use of the system

Scenario: a company user can register with sufficient information
Given mobile 13800138000, password testpass
And company name taobao, bank account 2208888888888888888 from bank BOC, taxpayer 51019221212, address Danver Street, 2nd, RM202
When I register
Then register success
And company information returned