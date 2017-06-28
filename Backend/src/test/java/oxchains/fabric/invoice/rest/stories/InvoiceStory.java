package oxchains.fabric.invoice.rest.stories;

import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import oxchains.fabric.invoice.rest.steps.InvoiceSteps;
import oxchains.fabric.invoice.rest.steps.UserSteps;

/**
 * @author aiet
 */
public class InvoiceStory {

    @Steps UserSteps userSteps;
    @Steps InvoiceSteps invoiceSteps;

    @Given("company user $companyUser")
    public void givenCompanyUser(String companyUser) {
        userSteps.userEnrolled(companyUser);
    }

    @Given("user $customer")
    public void givenUser(String customer) {
        userSteps.userEnrolled(customer);
    }

    @Given("$quantity goods named $name with price $price sold")
    public void givenGoodsSold(int quantity, String name, int price){
        invoiceSteps.givenGoods(name, price, quantity);
    }

    @When("$issuer issue invoice to $target for $customer")
    public void whenIIssueInvoiceFor(String issuer, String target, String customer) {
        invoiceSteps.issueTo(issuer, target, customer);
    }

    @Then("invoice issued")
    public void thenInvoiceIssued() {
        invoiceSteps.issued();
    }

    @Then("the invoice is present")
    public void thenInvoicePresent() {
        invoiceSteps.invoicePresent();
    }

    @When("$customer check invoice list")
    public void whenCheckInvoiceList(String customer) {
        invoiceSteps.invoiceListOf(customer);
    }

    @Given("invoice of $customer")
    public void givenInvoice(String customer){
        invoiceSteps.givenInvoiceOf(customer);
    }

    @When("$customer transfer invoice to $anotherCustomer")
    public void whenTransferInvoice(String customer, String anotherCustomer){
        invoiceSteps.transferInvoice(customer, anotherCustomer);
    }

    @Then("invoice transfered")
    public void thenInvoiceTransfered(){
        invoiceSteps.success();
    }

    @When("$customer query history of invoice $serial")
    public void whenQueryHistory(String customer, String serial){
        invoiceSteps.historyOfInvoiceFrom(serial, customer);
    }

    @Then("$customer in the history")
    public void thenHistoryContains(String customer){
        invoiceSteps.historyContains(customer);
    }

}
