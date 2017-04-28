package oxchains.fabric.invoice.rest.steps;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import oxchains.MgtConsoleApplication;
import oxchains.invoice.domain.Company;
import oxchains.invoice.domain.CompanyUser;
import oxchains.invoice.domain.User;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

/**
 * @author aiet
 */
@ContextConfiguration(classes = MgtConsoleApplication.class)
@TestPropertySource(locations = "classpath:test.properties")
public class RegisterSteps {

    private MockMvcRequestSpecification mockMvcRequest;
    private MockMvcResponse mockMvcResponse;
    private User user;
    private Company company;

    @Step("phone{0}, pass: {1}")
    public void givenAccount(String phone, String password) {
        user = new User(phone, password);
    }

    @Step("company: {0} at {4}, bank info: {1}-{2}, paying tax with {3}")
    public void fillCompanyInfo(String companyName, String bankAccount, String bankName, String taxId, String address) {
        company = new Company(companyName, taxId, address, bankName, bankAccount);
        CompanyUser companyUser = new CompanyUser();
        companyUser.setCompany(company);
        companyUser.setUser(user);
        mockMvcRequest = given()
          .body(companyUser)
          .contentType(ContentType.JSON);
    }

    @Step("registering")
    public void register() {
        mockMvcResponse = mockMvcRequest
          .when()
          .post("/company");
    }

    @Step("register succeeded")
    public void requestSuccess() {
        mockMvcResponse
          .then()
          .statusCode(HttpStatus.SC_OK)
          .and()
          .body("status", is(1));
    }

    @Step("company information verified")
    public void companyInfoVerified() throws Exception {
        mockMvcResponse
          .then()
          .body("data.address", equalTo(company.getAddress()))
          .and()
          .body("data.name", equalTo(company.getName()))
          .and()
          .body("data.account", equalTo(company.getBankAccount()))
          .and()
          .body("data.bank", equalTo(company.getBankName()))
          .and()
          .body("data.taxpayer", equalTo(company.getTaxIdentifier()));
    }
}
