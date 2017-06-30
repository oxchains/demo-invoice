package oxchains.fabric.invoice.rest.stories;

import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import oxchains.fabric.invoice.rest.steps.UserSteps;

/**
 * @author aiet
 */
public class CompanyRegisterStory {

    @Steps private UserSteps userSteps;

    @Given("mobile $phone, password $password")
    public void givenMobileAndPass(String phone, String password) {
        userSteps.givenAccount(phone, password, null);
    }

    @Given("company name $companyName, bank account $bankAccount from bank $bankName, taxpayer $taxIdentifier, address $address")
    public void givenCompanyInformation(String companyName, String bankAccount, String bankName, String taxId, String address) {
        userSteps.fillCompanyInfo(companyName, bankAccount, bankName, taxId, address);
    }

    @When("I register company")
    public void whenIRegister() {
        userSteps.registerCompany();
    }

    @Then("company registration success")
    public void thenRegisterSuccess() {
        userSteps.requestSuccess();
    }

    @Then("company information returned")
    public void thenCompanyInfoGot() throws Exception {
        userSteps.companyInfoVerified();
    }

    @When("I check company list")
    public void whenCheckCompanyList() {
        userSteps.companyList();
    }

    @Then("company $company is present")
    public void thenCompanyPresent(String company) {
        userSteps.includeCompany(company);
    }

    @When("I enroll company user")
    public void whenEnrollCompanyUser() {
        userSteps.enrollCompanyUser();
    }

    @Then("company user $user enrolled")
    public void thenEnrolled(String user) {
        userSteps.enrolled(user);
    }

}
