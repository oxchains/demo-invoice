user can see the due bills

Meta:

Narrative:
As a user
I want to check the due bills
So that I can handle them

GivenStories: stories/bill_issue_test.story

Scenario: a user can endorse the bill to others
Given bill in endorsement list of b
When I check for due bills
Then there is no due bills
When I wait 50 seconds for the bills to due
And I check for due bills
Then there is due bills
