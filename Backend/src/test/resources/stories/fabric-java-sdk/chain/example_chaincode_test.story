example chaincode manipulation

Meta:

Narrative:
As a fabric user
I want to install chaincode on peers
So that I can run the chaincode

Scenario: chaincode can be installed on peers
Given chain example-chain created at orderer grpc://10.8.47.90:7050
And peer peer0 at grpc://10.8.47.90:7051
And peer0 joined chain example-chain
And chaincode example_chaincode of version 0.1
When I install chaincode on chain example-chain
Then installation succeed

Scenario: installed chaincode can be instantiated, invoked, and queried
Given chain example-chain listens event-hub peer0 at grpc://10.8.47.90:7053
When I instantiate chaincode example_chaincode with: init a 10 b 20
Then intantiation succeed
When I query asset a with: query a
Then should return 10
When I transfer asset from a to b with: invoke a b 5
Then transfer succeed
When I query asset a with: query a
Then should return 5
When I query asset b with: query b
Then should return 25
