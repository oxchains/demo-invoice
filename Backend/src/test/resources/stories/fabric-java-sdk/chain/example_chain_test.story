create example chain foo

Meta:

Narrative:
As a fabric user
I want to build a chain
So that I can make trasactions on it

Scenario: fabric client can build chain with chain configuration
Given fabric client
And orderer at grpc://10.8.47.90:7050
When I construct a chain foo
Then the chain foo is created at orderer grpc://10.8.47.90:7050