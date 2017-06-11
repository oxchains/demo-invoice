# Billing

## Chaincode & REST API

chaincode | arguments | REST | description
----------|-----------|------|------------
checkdue | | N/A | check if a bill is due 
delete | {key} | N/A | delete an entity
query | {key} | N/A | get shim state information
register | {entity, account balance} | N/A | register a new user
getuserid | {entity} | N/A | return id for user name
billregister | {entity, due date, price, payer, payee, transferable} | POST `/bill` | register a bill
promptacceptance | {entity, bill number, action} | POST/PUT `/bill/acceptance` | prompt for acceptance
queryacceptance | {entity} | N/A | query acceptance status
promptwarrant | {entity, bill number, warrantor, action:1,0} | POST/PUT `/bill/warrant` | prompt for warrant
querywarrant | {entity} | N/A | query warrant information 
promptrevoke | {entity, bill number} | POST/PUT `/bill/revocation` | prompt for revocation
queryrevoke | {entity} | N/A | query revocation status
promptreceive | {entity, bill number, action:-1,0,1} | POST/PUT `/bill/reception` | prompt for reception
queryreceive | {entity} | N/A | query reception information
promptendorsement | {entity, bill number, endorsee, action:-1,0,1} | POST/PUT `/bill/endorsement` | prompt for endorsement
queryendorsement | {entity} | N/A | query endorsement status
promptpledge | {entity, bill number, pledgee, action:0,1} | POST/PUT `/bill/pledge` | prompt for pledge
querypledge | {entity} | N/A | query pledge information
promptpledgerelease | {entity, bill number, action:0,1} | POST/PUT `/bill/pledge/release` | prompt for pledge release
querypledgerelease | {entity} | N/A | query pledge status
promptdiscount | {entity, bill number, bank, action:0,1, type, insterest, amount} | POST/PUT `/bill/discount` | prompt for discount
querydiscount | {entity} | N/A | query discount information
promptpayment | {entity, bill number, action:-1,0,1} | POST/PUT `/bill/payment` | prompt for payment
querypayment | {entity} | N/A | query payment status
promptrecourse | {entity, bill number, borrower, action: 0} | POST/PUT `/bill/recourse` | prompt for recourse
queryrecourse | {entity} | N/A | query for recourse information





