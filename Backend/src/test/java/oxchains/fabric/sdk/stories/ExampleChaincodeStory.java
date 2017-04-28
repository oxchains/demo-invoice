package oxchains.fabric.sdk.stories;

import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import oxchains.fabric.sdk.steps.ChaincodeAPISteps;
import oxchains.fabric.sdk.steps.PeerAPISteps;

/**
 * @author aiet
 */
public class ExampleChaincodeStory {

    @Steps ChaincodeAPISteps chaincodeAPISteps;
    @Steps PeerAPISteps peerAPISteps;

    @Given("chaincode $chaincodeName of version $chaincodeVersion")
    public void givenChaincode(String chaincodeName, String chaincodeVersion) {
        chaincodeAPISteps.loadChaincode(chaincodeName, chaincodeVersion, chaincodeName);
    }

    @Given("$peerId joined chain $chainName")
    public void whenPeerJoinChain(String peerId, String chainName) {
        peerAPISteps.peerJoinChain(peerId, chainName);
    }


    @Given("chain $chainName listens event-hub $eventName at $eventEndpoint")
    public void givenEventhub(String chainName, String eventName, String eventEndpoint) throws Exception {
        chaincodeAPISteps.checkChain(chainName);
        chaincodeAPISteps.chainWithEventHub(eventName, eventEndpoint);
    }

    @When("I install chaincode on chain $chainName")
    public void whenInstallChaincodeOnPeer(String chainName) {
        chaincodeAPISteps.installChaincodeOnPeer(chainName);
    }

    @Then("installation succeed")
    public void thenInstallationSucceed() {
        chaincodeAPISteps.installationSucceeded();
    }

    @When("I instantiate chaincode $chaincodeName with: $arg")
    public void whenInstantiateChaincodeWith(String chaincodeName, String arg) throws Exception {
        chaincodeAPISteps.instantiateWith(chaincodeName, arg);
    }

    @Then("intantiation succeed")
    public void thenIntantiationSucceed() {
        chaincodeAPISteps.instantiationSucceeded();
    }

    @When("I query asset $holder with: $arg")
    public void whenIQueryForAssetOf(String holder, String arg) {
        chaincodeAPISteps.queryChainWith(arg);
    }

    @Then("should return $result")
    public void thenShouldReturn(int result) {
        chaincodeAPISteps.resultIs(result);
    }

    @When("I transfer asset from a to b with: $arg")
    public void whenTransferAsset(String arg) throws Exception {
        chaincodeAPISteps.invokeChainWith(arg);
    }

    @Then("transfer succeed")
    public void thenTransferSucceed() {
        chaincodeAPISteps.invokeSucceeded();
    }

}
