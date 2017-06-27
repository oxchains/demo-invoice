package oxchains.fabric.invoice.rest.steps;

import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.response.ExtractableResponse;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import oxchains.EInvoiceApplication;
import oxchains.invoice.domain.Company;
import oxchains.invoice.domain.CompanyUser;
import oxchains.invoice.domain.User;
import oxchains.invoice.rest.domain.NameAndPass;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNotNull;

/**
 * @author aiet
 */
@ContextConfiguration(classes = EInvoiceApplication.class)
@TestPropertySource(locations = "classpath:test.properties")
public class UserSteps {

    private MockMvcResponse mockMvcResponse;
    private static User user;
    private CompanyUser companyUser;
    static Map<String, String> userTokens = new HashMap<>();

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
        companyUser.withUser(user);
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

    public void companyList() {
        mockMvcResponse = given()
          .when()
          .get("/company");
    }

    public void includeCompany(String company) {
        requestSuccess();
        mockMvcResponse
          .then()
          .body("data.name", hasItem(company));
    }

    public void enroll() {
        NameAndPass nameAndPass = new NameAndPass();
        nameAndPass.setUsername(user.getName());
        nameAndPass.setPassword(user.getPassword());
        nameAndPass.setBiz(false);
        mockMvcResponse = given()
          .body(nameAndPass)
          .contentType(JSON)
          .when()
          .post("/token");
    }

    public void enrolled(String user) {
        requestSuccess();
        ExtractableResponse extractableResponse = mockMvcResponse
          .then()
          .body("data.token", notNullValue())
          .extract();
        userTokens.put(user, "Bearer " + extractableResponse
          .jsonPath()
          .getString("data.token"));
    }

    public void enrollCompanyUser() {
        NameAndPass nameAndPass = new NameAndPass();
        nameAndPass.setUsername(companyUser.getName());
        nameAndPass.setPassword(companyUser.getPassword());
        nameAndPass.setBiz(true);
        mockMvcResponse = given()
          .body(nameAndPass)
          .contentType(JSON)
          .when()
          .post("/token");
    }

    public void userEnrolled(String companyUser) {
        assertNotNull("user "+ companyUser + "should have enrolled", userTokens.containsKey(companyUser));
    }
}
