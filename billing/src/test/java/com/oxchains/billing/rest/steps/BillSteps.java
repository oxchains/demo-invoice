package com.oxchains.billing.rest.steps;

import com.jayway.jsonpath.JsonPath;
import com.oxchains.billing.domain.Bill;
import net.minidev.json.JSONArray;
import org.springframework.core.ResolvableType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.Assert.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

/**
 * @author aiet
 */
public class BillSteps {


  private WebTestClient client = WebTestClient.bindToServer().baseUrl("http://localhost:17173").build();
  private ResponseSpec response;
  private String respString;

  public void listAcceptance() {
    respString = new String(client.get().uri("/bill/a/acceptance").exchange().returnResult(ResolvableType.forClass(String.class)).getResponseBodyContent());
  }

  public void listEmpty() {
    assertNotNull(respString);
    String payload = JsonPath.parse(respString).read(JsonPath.compile("$.data.payload"));
    JSONArray jsonArray = JsonPath.parse("[" + payload + "]").read(JsonPath.compile("$"));
    for (Object j : jsonArray) {
      assertTrue("payload should be empty", ((JSONArray) j).isEmpty());
    }
  }

  public void issueBill(String payer, String payee, String price, String due) {
    Bill bill = new Bill();
    bill.setDrawee(payer);
    bill.setDrawer(payer);
    bill.setPayee(payee);
    bill.setPrice(price);
    bill.setDue(Date.from(LocalDateTime.now().plusDays(4).atZone(ZoneId.systemDefault()).toInstant()));
    bill.setTransferable("");
    response = client.post().uri("/bill").contentType(APPLICATION_JSON_UTF8).body(fromObject(bill)).exchange();
  }

  public void success() {
    response.expectStatus().is2xxSuccessful().expectBody().jsonPath("$.data.success").isEqualTo(1);
  }

  public void listNotEmpty() {
    assertNotNull(respString);
    String payload = JsonPath.parse(respString).read(JsonPath.compile("$.data.payload"));
    JSONArray jsonArray = JsonPath.parse("[" + payload + "]").read(JsonPath.compile("$"));
    boolean empty = true;
    for (Object j : jsonArray) {
      empty = empty && ((JSONArray) j).isEmpty();
    }
    assertFalse("list should not be empty", empty);
  }

}
