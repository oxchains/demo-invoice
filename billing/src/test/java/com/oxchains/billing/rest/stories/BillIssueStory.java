package com.oxchains.billing.rest.stories;

import com.oxchains.billing.rest.steps.BillSteps;
import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

/**
 * @author aiet
 */
public class BillIssueStory {

  @Steps
  BillSteps billSteps;

  @When("I list $action of $user")
  public void whenIListBill(String action, String user) {
    billSteps.list(action, user);
  }

  @Then("no bill in the list")
  public void thenNoBillInTheList() {
    billSteps.listEmpty();
  }

  @When("I issue bill of price $price to $payee as $payer, due $due seconds")
  public void whenIIssueBill(String price, String payee, String payer, String due) {
    billSteps.issueBill(payer, payee, price, due);
  }

  @Then("bill registered")
  public void thenBillRegistered() {
    billSteps.success();
  }

  @Then("the new bill is in the list")
  public void thenTheNewBillIsInTheList() {
    billSteps.listNotEmpty();
  }

}
