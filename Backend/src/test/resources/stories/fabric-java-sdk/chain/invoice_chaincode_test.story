invoice chaincode manipulation

Meta:

Narrative:
As a invoice backend manager
I want to install invoice chaincode on peers
So that I can run the chaincode

Scenario: chaincode invoice can be installed on peers
Given chain invoice-chain created at orderer grpc://10.8.47.90:7050
And peer peer0 at grpc://10.8.47.90:7051
And peer0 joined chain invoice-chain
And chaincode invoice_chaincode of version 0.1
When I install chaincode on chain invoice-chain
Then installation succeed

Scenario: invoice can be created, transferred, queried, reimbursed
Given invoice chain invoice-chain
And listens event-hub peer0 at grpc://10.8.47.90:7053
When I instantiate invoice chaincode invoice_chaincode with init
Then invoice intantiation succeed
When I create invoice with: create 00001 JD PKU {%Math.floor(Date.now()/1000)%} metadata
Then creation success
When I query invoice: myHistory JD 1
Then invoice should contain 00001
When I transfer invoice: transfer 00001 JD xfja {%Math.floor(Date.now()/1000)%}
Then invoice transfer succeed
When I query invoice: myHistory JD 1
Then invoice should contain 00001
When I query invoice: myHistory xfja 0
Then invoice should contain 00001
When I request a reimbursement with createbx 99999 00001 xfja PKU {%Math.floor(Date.now()/1000)%} bxinfo {%Math.floor(Date.now()/1000)+3600%}
Then reimbursement request accepted

Scenario: reimbursement can be queried, rejected
Given invoice chain invoice-chain
And invoice chaincode invoice_chaincode
When I query reimbursement with getbx 99999 xfja
Then invoice should contain 00001
When I query reimbursement with getbx 99999 PKU
Then invoice should contain 00001
When I reject reimbursement with rejectbx 99999 PKU invalid {%Math.floor(Date.now()/1000)%}
Then reimbursement 99999 has been rejected
When I query reimbursement with getReimburseInfo 00001 PKU
Then invoice should not contain 99999

Scenario: reimbursement can be queried, confirmed
Given invoice chain invoice-chain
And invoice chaincode invoice_chaincode
When I request a reimbursement with createbx 88888 00001 xfja PKU {%Math.floor(Date.now()/1000)%} bxinfo {%Math.floor(Date.now()/1000)+3600%}
Then reimbursement request accepted
When I query reimbursement with getbx 88888 xfja
Then invoice should contain 00001
When I query reimbursement with getbx 88888 PKU
Then invoice should contain 00001
When I confirm reimbursement with confirmbx 88888 PKU {%Math.floor(Date.now()/1000)%}
Then reimbursement 88888 has been confirmed
When I query invoice: myHistory PKU 1
Then invoice should contain 00001
When I query reimbursement with getReimburseInfo 00001 PKU
Then invoice should contain xfja
When I query reimbursement with getReimburseInfo 00001 xfja
Then invoice should contain PKU
When I query reimbursement with getReimburseInfo 00001 JD
Then query fail
When I transfer invoice: transfer 00001 xfja dummy {%Math.floor(Date.now()/1000)%}
Then invoice transfer fail
