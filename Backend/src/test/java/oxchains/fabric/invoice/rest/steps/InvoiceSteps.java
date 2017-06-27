package oxchains.fabric.invoice.rest.steps;

import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import oxchains.invoice.domain.Goods;
import oxchains.invoice.domain.Invoice;
import oxchains.invoice.rest.domain.InvoiceWithGoods;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author aiet
 */
public class InvoiceSteps {

    private Goods goods;
    private MockMvcResponse mockMvcResponse;
    private Invoice invoice;
    private String history;
    private String reimburseId;

    public void issueTo(String issuer, String customer) {
        InvoiceWithGoods invoiceWithGoods = new InvoiceWithGoods();
        invoiceWithGoods.setName(goods.getName());
        invoiceWithGoods.setDescription(goods.getDescription());
        invoiceWithGoods.setPrice(goods.getPrice());
        invoiceWithGoods.setQuantity(goods.getQuantity());
        invoiceWithGoods.setTitle(customer);
        invoiceWithGoods.setIssuer(issuer);
        mockMvcResponse = given()
          .body(invoiceWithGoods)
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
          .getObject("$.data", Invoice.class);
    }

    public void invoiceListOf(String customer) {
        mockMvcResponse = given()
          .when()
          .get("/invoice");
    }

    public void invoicePresent() {
        success();
        //TODO
    }

    public void givenInvoiceOf(String serial, String customer) {
        invoice = given()
          .when()
          .get("/invoice/" + serial)
          .then()
          .statusCode(SC_OK)
          .and()
          .body("status", is(1))
          .and()
          .extract()
          .body()
          .jsonPath()
          .getObject("$.data", Invoice.class);
    }

    public void transferInvoice(String customer, String anotherCustomer) {
        mockMvcResponse = given()
          .when()
          .put("/invoice");
        //TODO
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

    public void reimburse(String customer, String serial) {
        //TODO
        mockMvcResponse = given()
          .queryParam("invoices", serial)
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
        reimburseId = success().extract().body().jsonPath().getString("data");
    }

}
