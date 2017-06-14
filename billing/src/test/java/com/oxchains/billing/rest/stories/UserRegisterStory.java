package com.oxchains.billing.rest.stories;

import com.oxchains.billing.rest.steps.UserSteps;
import net.thucydides.core.annotations.Steps;
import org.apache.commons.lang3.RandomStringUtils;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Before;

/**
 * @author aiet
 */
public class UserRegisterStory {


  @Steps
  UserSteps userSteps;

  @Before
  public void setup() {
  }

  @Given("system initialized with user $user1 and $user2")
  public void givenSystemInitializedWithUser(String user1, String user2) {
    userSteps.hasUser(user1, user2);
  }

  @When("I register user $user")
  public void whenIRegister(String user) {
    userSteps.register(user);
  }

  @When("I register a new user")
  public void whenIRegister() {
    userSteps.register();
  }

  @When("I delete user $user")
  public void whenIDelete(String user) {
    userSteps.delete(user);
  }

  @Then("registration fail")
  public void thenRegistrationFail() {
    userSteps.registrationFail();
  }

  @Then("user $user is not in the system")
  public void thenNotInTheSystem(String user) {
    userSteps.userNotFound(user);
  }


  @Then("a new user is not in the system")
  public void thenNotInTheSystem(){
    userSteps.userNotFound();
  }

  @Then("the new user is in the system")
  public void thenNewUserInTheSystem(){
    userSteps.userFound();
  }

  @Then("user $user is in the system")
  public void thenIsInTheSystem(String user) {
    userSteps.userFound(user);
  }


}
