package oxchains.fabric.sdk.stories;

import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import oxchains.fabric.sdk.steps.ChaincodeAPISteps;

import static oxchains.fabric.util.StoryTestUtil.scriptParse;

/**
 * @author aiet
 */
public class InvoiceChaincodeStory {

    @Steps ChaincodeAPISteps chaincodeSteps;


    @Given("invoice chain $chainName")
    public void givenInvoiceChain(String chainName){
        chaincodeSteps.checkChain(chainName);
    }

    @Given("invoice chaincode $chaincode")
    public void givenInvoiceChaincode(String chaincode){
        chaincodeSteps.checkChaincode(chaincode);
    }

    @Given("listens event-hub $eventName at $eventEndpoint")
    public void givenEventhub(String eventName, String eventEndpoint) throws Exception {
        chaincodeSteps.chainWithEventHub(eventName, eventEndpoint);
    }

    @When("I instantiate invoice chaincode $chaincodeName with $arg")
    public void whenInstantiateChaincodeWith(String chaincodeName, String arg) throws Exception {
        chaincodeSteps.instantiateWith(chaincodeName, arg);
    }

    @Then("invoice intantiation succeed")
    public void thenIntantiationSucceed() {
        chaincodeSteps.instantiationSucceeded();
    }

    @When("I create invoice with: $args")
    public void whenICreateInvoiceWith(String args) throws Exception {
        chaincodeSteps.invokeChainWith(scriptParse(args));
    }

    @Then("creation success")
    public void thenCreateSucceeded() {
        chaincodeSteps.invokeSucceeded();
    }

    @When("I $action invoice: $args")
    public void whenIQueryInvoiceWith(String action, String args) throws Exception {
        switch (action) {
        case "query":
            chaincodeSteps.queryChainWith(args);
            break;
        case "transfer":
            chaincodeSteps.invokeChainWith(scriptParse(args));
            break;
        default:
            break;
        }
    }

    @When("I request a reimbursement with $args")
    public void whenICreateReimbursementWith(String args) throws Exception {
        chaincodeSteps.invokeChainWith(scriptParse(args));
    }

    @Then("reimbursement request accepted")
    public void thenRequestAccepted() {
        chaincodeSteps.invokeSucceeded();
    }

    @When("I reject reimbursement with $arg")
    public void whenIRejectReimbursementWith(String arg) throws Exception {
        chaincodeSteps.invokeChainWith(scriptParse(arg));
    }

    @When("I query reimbursement with $arg")
    public void whenIQueryReimbursementWith(String arg) {
        chaincodeSteps.queryChainWith(arg);
    }

    @When("I confirm reimbursement with $arg")
    public void whenIConfirmReimbursementWith(String arg) throws Exception {
        chaincodeSteps.invokeChainWith(scriptParse(arg));
    }

    @Then("reimbursement $bxid has been $action")
    public void thenReimbursementActionDone(String bxid, String action) {
        chaincodeSteps.invokeSucceeded();
    }

    @Then("invoice should not contain $invoiceId")
    public void thenShouldNotContain(String invoiceId) {
        chaincodeSteps.resultContainsNo(invoiceId);
    }

    @Then("invoice should contain $invoiceId")
    public void thenShouldContain(String invoiceId) {
        chaincodeSteps.resultContains(invoiceId);
    }

    @Then("invoice transfer succeed")
    public void thenTransferSucceed() {
        chaincodeSteps.invokeSucceeded();
    }

    @Then("invoice transfer fail")
    public void thenTransferFail(){
        chaincodeSteps.invokeFailed();
    }

    @Then("query fail")
    public void thenQueryFail(){
        chaincodeSteps.queryFail();
    }

}
