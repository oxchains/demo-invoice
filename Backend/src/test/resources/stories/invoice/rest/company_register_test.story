Company Register

Narrative:
As a company user
I want to register
So that I can make use of the system

Scenario: a company user can register with sufficient information
Meta: @id1 taobao
Given mobile 13800138000, password testpass
And company name taobao, bank account 2208888888888888888 from bank BOC, taxpayer 51019221212, address Danver Street, 2nd, RM202
When I register company
Then company registration success
And company information returned
When I check company list
Then company taobao is present
When I enroll company user
Then company user taobao enrolled

Scenario: a company user can register with sufficient information
Meta: @id2 xfja
Given mobile 13800138000, password testpass
And company name xfja, bank account 220999999999999999 from bank ICBC, taxpayer 51019333333, address Oak Road, 4th, RM110
When I register company
Then company registration success
And company information returned
When I check company list
Then company taobao is present
When I enroll company user
Then company user xfja enrolled
