Invoice Issue and List

Narrative:
As a company user
I want to issue invoice
So that I can prove the goods are sold to the customer

Scenario: a company user can issue invoice for customers
Given company user taobao
And user JD
And 5 goods named computer with price 4999 sold
When taobao issue invoice for JD
Then invoice issued
When taobao check invoice list
Then the invoice is present
When JD check invoice list
Then the invoice is present

Scenario: a user can transfer invoice to others
Given user JD
And user oxchains
And invoice 1 of JD
When JD transfer invoice to oxchains
Then invoice transfered
When JD query history of invoice 1
Then oxchains in the history


