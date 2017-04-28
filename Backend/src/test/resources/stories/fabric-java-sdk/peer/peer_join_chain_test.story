create chain bar and let peers join

Meta:

Narrative:
As a fabric user
I want to have peers
So that I can join a chain for transactions

Scenario: Fabric peer can join built chain
Given chain bar created at orderer grpc://10.8.47.90:7050
And peer peer0 at grpc://10.8.47.90:7051
When peer0 joins chain bar
Then peer0 has joined bar

Scenario: Invalid peer cannot join built channel
Given peer peerx at grpc://127.0.0.1:7051
When peerx joins chain bar
Then peerx cannot join bar
