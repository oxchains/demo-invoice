package com.oxchains.billing.rest.stories;

import com.oxchains.billing.rest.steps.BillSteps;
import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

/**
 * @author aiet
 */
public class BillDueStory {

  @Steps
  BillSteps dueSteps;

  @When("I check for due bills")
  public void whenICheckForDueBills() {
    dueSteps.checkDue();
  }

  @Then("there is no due bills")
  public void thenThereIsNoDueBills() {
    dueSteps.noBillsDue();
  }

  @When("I wait $seconds seconds for the bills to due")
  public void whenIWaitForTheBillsToDue(String seconds) throws Exception {
    dueSteps.waitForDue(seconds);
  }

  @Then("there is due bills")
  public void thenThereIsDueBills() {
    dueSteps.billsDue();
  }

}
