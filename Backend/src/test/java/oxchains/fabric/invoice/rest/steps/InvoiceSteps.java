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

    public void invoicePresent() {
        success().body("data.serial", hasItem(invoice.getSerial()));
    }

    public void givenInvoiceOf(String customer) {
        given()
          .header(AUTHORIZATION, userTokens.get(customer))
          .when()
          .get("/invoice/" + invoice.getSerial())
          .then()
          .statusCode(SC_OK)
          .and()
          .body("status", is(1));

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

    public void historyOfInvoiceFrom(String serial, String customer) {
        mockMvcResponse = given()
          .when()
          .get("/invoice/" + serial + "/history");
        history = success()
          .extract()
          .jsonPath()
          .getString("data");
    }

    public void historyContains(String customer) {
        assertThat(history, containsString(customer));
    }

    public void reimburse(String customer) {
        //TODO
        mockMvcResponse = given()
          .queryParam("invoices", "")
          .when()
          .post("/reimbursement");
    }

    public void reimbursementListOf(String customer) {
        //TODo
        mockMvcResponse = given()
          .when()
          .get("/reimbursement");
    }

    public void reimbursementPresent(boolean yes) {
        success();//TODO
    }

    public void rejectReimbursement(String company) {
        mockMvcResponse = given()
          .queryParam("action", 0)
          .queryParam("ids", reimburseId)
          .when()
          .put("/reimbursement");
        //TODO
    }

    public void confirmReimbursementBy(String company) {
        mockMvcResponse = given()
          .queryParam("action", 1)
          .queryParam("ids", reimburseId)
          .when()
          .put("/reimbursement");
        //TODO
    }

    public void reimbursementCreated() {
        reimburseId = success()
          .extract()
          .body()
          .jsonPath()
          .getString("data");
    }

}
