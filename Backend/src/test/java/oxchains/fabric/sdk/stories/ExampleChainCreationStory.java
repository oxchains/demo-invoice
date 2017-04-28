package oxchains.fabric.sdk.stories;

import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import oxchains.fabric.sdk.steps.ChainAPISteps;

import java.io.IOException;

/**
 * @author aiet
 */
public class ExampleChainCreationStory {

    @Steps ChainAPISteps chainAPISteps;

    @Given("fabric client")
    public void givenFabricClientAndChainConfiguration() {
        chainAPISteps.createFabricClient();
    }

    @Given("orderer at $ordererEndpoint")
    public void givenOrdererAt(String ordererEndpoint) {
        chainAPISteps.buildOrdererFrom(ordererEndpoint);
    }

    @When("I construct a chain $chainName")
    public void whenIConstructAChainWithOrderer(String chainName) throws IOException {
        chainAPISteps.constructChainOf(chainName);
    }

    @Then("the chain $chainName is created at orderer $ordererEndpoint")
    public void thenTheChainIsCreated(String chainName, String ordererEndpoint) {
        chainAPISteps.chainCreated(chainName, ordererEndpoint);
    }

}
