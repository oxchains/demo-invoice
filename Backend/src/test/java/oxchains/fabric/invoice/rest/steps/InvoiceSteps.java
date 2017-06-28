package oxchains.fabric.invoice.rest.steps;

import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import oxchains.invoice.domain.Goods;
import oxchains.invoice.domain.Invoice;
import oxchains.invoice.rest.domain.InvoiceReq;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.util.Collections.singletonList;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static oxchains.fabric.invoice.rest.steps.UserSteps.userTokens;

/**
 * @author aiet
 */
public class InvoiceSteps {

    private Goods goods;
    private MockMvcResponse mockMvcResponse;
    private static Invoice invoice;
    private String history;
    private String reimburseId;

    public void issueTo(String issuer, String target, String customer) {
        InvoiceReq invoice = new InvoiceReq();
        invoice.setGoods(singletonList(goods));
        invoice.setTitle(target);
        invoice.setTarget(customer);
        mockMvcResponse = given()
          .body(invoice)
          .header(AUTHORIZATION, userTokens.get(issuer))
          .contentType(JSON)
          .when()
          .post("/invoice");
    }

    public void givenGoods(String name, int price, int quantity) {
        goods = new Goods();
        goods.setName(name);
        goods.setDescription(name);
        goods.setPrice(price);
        goods.setQuantity(quantity);
    }

    public void issued() {
        invoice = mockMvcResponse
          .then()
          .statusCode(SC_OK)
          .and()
          .body("status", is(1))
          .and()
          .extract()
          .jsonPath()
          .getObject("data", Invoice.class);
    }

    public void invoiceListOf(String customer) {
        mockMvcResponse = given()
          .header(AUTHORIZATION, userTokens.get(customer))
          .when()
          .get("/invoice");
    }

    public void invoicePresent(boolean yes) {
        success().body("data.serial", yes ? hasItem(invoice.getSerial()) : not(hasItem(invoice.getSerial())));
    }

    public void givenInvoiceOf(String customer) {
        invoice = given()
          .header(AUTHORIZATION, userTokens.get(customer))
          .when()
          .get("/invoice/" + invoice.getSerial())
          .then()
          .statusCode(SC_OK)
          .and()
          .body("status", is(1))
          .extract()
          .jsonPath()
          .getObject("data", Invoice.class);

    }

    public void transferInvoice(String customer, String anotherCustomer) {
        mockMvcResponse = given()
          .header(AUTHORIZATION, userTokens.get(customer))
          .queryParam("invoice", invoice.getSerial())
          .queryParam("target", anotherCustomer)
          .when()
          .put("/invoice");
    }

    public ValidatableMockMvcResponse success() {
        return mockMvcResponse
          .then()
          .statusCode(SC_OK)
          .and()
          .body("status", is(1));
    }

    public void reimburse(String customer, String company) {
        mockMvcResponse = given()
          .queryParam("invoices", invoice.getSerial())
          .queryParam("company", company)
          .header(AUTHORIZATION, userTokens.get(customer))
          .when()
          .post("/reimbursement");
    }

    public void reimbursementListOf(String customer) {
        mockMvcResponse = given()
          .header(AUTHORIZATION, userTokens.get(customer))
          .when()
          .get("/reimbursement");
    }

    public void reimbursementPresent(boolean yes) {
        success().body("data.serial", yes ? hasItem(reimburseId) : not(hasItem(reimburseId)));
    }

    public void rejectReimbursement(String company) {
        mockMvcResponse = given()
          .queryParam("action", 0)
          .queryParam("id", reimburseId)
          .header(AUTHORIZATION, userTokens.get(company))
          .when()
          .put("/reimbursement");
    }

    public void confirmReimbursementBy(String company) {
        mockMvcResponse = given()
          .queryParam("action", 1)
          .queryParam("id", reimburseId)
          .header(AUTHORIZATION, userTokens.get(company))
          .when()
          .put("/reimbursement");
    }

    public void reimbursementCreated() {
        reimburseId = success()
          .extract()
          .body()
          .jsonPath()
          .getString("data.serial");
    }

    public void reimbursementOf(String company) {
        mockMvcResponse = given()
          .header(AUTHORIZATION, userTokens.get(company))
          .when()
          .get("/reimbursement/" + reimburseId);
    }

    public void invoicePresentInReimbursement(boolean yes) {
        success().body("data.invoices.serial", yes ? hasItem(hasItem(invoice.getSerial())) : not(hasItem(hasItem(invoice.getSerial()))));
    }
}
