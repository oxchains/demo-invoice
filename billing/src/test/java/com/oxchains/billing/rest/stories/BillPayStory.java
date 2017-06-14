package com.oxchains.billing.rest.stories;

import com.oxchains.billing.rest.steps.BillSteps;
import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

/**
 * @author aiet
 */
public class BillPayStory {

  @Steps
  BillSteps steps;

  @Given("bill in acceptance list of $user")
  public void givenBillInAcceptanceList(String user) {
    steps.billNotEmpty(user);
  }

  @When("I accept it as $user")
  public void whenIAcceptIt(String user) {
    steps.acceptBill(user);
  }


  @When("I present $action of the bill to $user as $by")
  public void whenIPresentActionTo(String action, String user, String by){
    steps.present(user, action, by);
  }

  @Then("the bill is in the $action list of $user")
  public void thenTheBillIsInTheListOf(String action, String user){
    steps.billInListOf(action, user);
  }

  @Then("the new bill is accepted by $user")
  public void thenTheNewBillIsAccepted(String user) {
    steps.billAccepted(user);
  }

  @When("I have the bill guaranteed by $user as $as")
  public void whenIHaveTheBillGuaranteedByUser(String user, String as) {
    steps.guaranteeBill(user, as);
  }


  @Then("the bill is guaranteed by $user")
  public void thenTheBillIsGuaranteed(String user){
    steps.billGuaranteed(user);
  }

}
