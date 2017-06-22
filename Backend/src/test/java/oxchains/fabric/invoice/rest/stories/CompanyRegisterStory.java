package oxchains.fabric.invoice.rest.stories;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Before;
import oxchains.fabric.invoice.rest.steps.RegisterSteps;
import oxchains.invoice.rest.CompanyController;

/**
 * @author aiet
 */
public class CompanyRegisterStory {

    @Before
    public void init() {
        RestAssuredMockMvc.standaloneSetup(new CompanyController());
    }

    @Steps RegisterSteps registerSteps;

    @Given("mobile $phone, password $password")
    public void givenMobileAndPass(String phone, String password) {
        registerSteps.givenAccount(phone, password);
    }

    @Given("company name $companyName, bank account $bankAccount from bank $bankName, taxpayer $taxIdentifier, address $address")
    public void givenCompanyInformation(String companyName, String bankAccount, String bankName, String taxId, String address) {
        registerSteps.fillCompanyInfo(companyName, bankAccount, bankName, taxId, address);
    }

    @When("I register")
    public void whenIRegister() {
        registerSteps.register();
    }

    @Then("register success")
    public void thenRegisterSuccess() {
        registerSteps.requestSuccess();
    }

    @Then("company information returned")
    public void thenCompanyInfoGot() throws Exception {
        registerSteps.companyInfoVerified();
    }
}
