package oxchains.fabric.invoice.rest.stories;

import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import oxchains.fabric.invoice.rest.steps.UserSteps;

/**
 * @author aiet
 */
public class UserRegisterStory {

    @Steps private UserSteps steps;

    @Given("name $name, mobile $phone, password $password")
    public void givenUser(String phone, String password, String name) {
        steps.givenAccount(phone, password, name);
    }

    @When("I register user")
    public void registerUser() {
        steps.registerUser();
    }

    @Then("user registration success")
    public void registered() {
        steps.requestSuccess();
    }

    @When("I enroll user")
    public void enroll(){
        steps.enroll();
    }

    @Then("user $user enrolled")
    public void enrolled(String user){
        steps.enrolled(user);
    }


}
