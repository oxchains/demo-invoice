Reimburse Issue and List

Narrative:
As a user
I want to issue invoice
So that I can prove the goods are sold to the customer

GivenStories: stories/invoice/rest/invoice_test.story

Scenario: a user can reimburse invoice
Given user oxchains
And company user xfja
And invoice of oxchains
When oxchains request reimbursement to xfja with the invoice
Then reimbursement created
When oxchains check reimbursement list
Then the reimbursement is present
And the reimbursing invoice is present
When xfja check reimbursement list
Then the reimbursement is present
And the reimbursing invoice is present
When xfja reject reimbursement
Then request success
When xfja check the reimbursement
Then the reimbursing invoice is not present
When oxchains request reimbursement to xfja with the invoice
Then reimbursement created
When xfja check reimbursement list
Then the reimbursement is present
When xfja confirm reimbursement
Then request success
When oxchains check reimbursement list
Then the reimbursing invoice is present
