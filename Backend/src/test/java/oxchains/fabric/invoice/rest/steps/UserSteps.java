package oxchains.fabric.invoice.rest.steps;

import io.restassured.module.mockmvc.response.MockMvcResponse;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import oxchains.EInvoiceApplication;
import oxchains.invoice.domain.Company;
import oxchains.invoice.domain.CompanyUser;
import oxchains.invoice.domain.User;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

/**
 * @author aiet
 */
@ContextConfiguration(classes = EInvoiceApplication.class)
@TestPropertySource(locations = "classpath:test.properties")
public class UserSteps {

    private MockMvcResponse mockMvcResponse;
    private static User user;
    private CompanyUser companyUser;

    @Step("phone{0}, pass: {1}, name: {2}")
    public void givenAccount(String phone, String password, String name) {
        user = new User(phone, password);
        if (name != null) user.setName(name);
    }

    @Step("company: {0} at {4}, bank info: {1}-{2}, paying tax with {3}")
    public void fillCompanyInfo(String companyName, String bankAccount, String bankName, String taxId, String address) {
        Company company = new Company(companyName, taxId, address, bankName, bankAccount);
        companyUser = new CompanyUser();
        companyUser.setCompany(company);
        user.setName(companyName);
        companyUser.setUser(user);
    }

    @Step("registering company")
    public void registerCompany() {
        mockMvcResponse = given()
          .body(companyUser)
          .contentType(JSON)
          .when()
          .post("/company");
    }

    @Step("registering user")
    public void registerUser() {
        mockMvcResponse = given()
          .body(user)
          .contentType(JSON)
          .when()
          .post("/user");
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
        Company company = companyUser.getCompany();
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
