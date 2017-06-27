Reimburse Issue and List

Narrative:
As a user
I want to issue invoice
So that I can prove the goods are sold to the customer

Scenario: a user can reimburse invoice
Given user JD
And company user taobao
And invoice of JD
When JD request reimbursement with the invoice
Then reimbursement created
When JD check reimbursement list
Then the reimbursement is present
When taobao check reimbursement list
Then the reimbursement is present
When taobao reject reimbursement
Then request success
When taobao check reimbursement list
Then the reimbursement is not present
When taobao confirm reimbursement
Then request success
When taobao check reimbursement list
Then the reimbursement is present
When JD check reimbursement list
Then the reimbursement is present
