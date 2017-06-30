package oxchains.fabric.invoice.rest.stories;

import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import oxchains.fabric.invoice.rest.steps.InvoiceSteps;

/**
 * @author aiet
 */
public class ReimburseStory {

    @Steps private InvoiceSteps steps;

    @When("$customer request reimbursement to $company with the invoice")
    public void whenIssueReimbursement(String customer, String company){
        steps.reimburse(customer, company);
    }

    @Then("reimbursement created")
    public void thenReimbursementDone(){
        steps.reimbursementCreated();
    }

    @Then("request success")
    public void thenRequestSuccess(){
        steps.success();
    }

    @When("$customer check reimbursement list")
    public void whenCheckReimbursementList(String customer){
        steps.reimbursementListOf(customer);
    }

    @Then("the reimbursement is present")
    public void thenReimbursementPresent(){
        steps.reimbursementPresent(true);
    }


    @When("$company reject reimbursement")
    public void whenRejectReimbursement(String company){
        steps.rejectReimbursement(company);
    }

    @Then("the reimbursement is not present")
    public void thenReimbursementNotPresent(){
        steps.reimbursementPresent(false);
    }

    @When("$company confirm reimbursement")
    public void whenConfirmReimbursement(String company){
        steps.confirmReimbursementBy(company);
    }

    @When("$company check the reimbursement")
    public void whenCheckReimbursement(String company){
        steps.reimbursementOf(company);
    }

    @Then("the reimbursing invoice is not present")
    public void thenInvoiceNotPresent(){
        steps.invoicePresentInReimbursement(false);
    }


    @Then("the reimbursing invoice is present")
    public void thenInvoicePresent(){
        steps.invoicePresentInReimbursement(true);
    }

}
