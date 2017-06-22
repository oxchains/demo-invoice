package com.oxchains.billing.rest.steps;

import com.jayway.jsonpath.JsonPath;
import com.oxchains.billing.domain.Bill;
import com.oxchains.billing.rest.common.*;
import net.minidev.json.JSONArray;
import org.springframework.core.ResolvableType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

/**
 * @author aiet
 */
public class BillSteps {


  private WebTestClient client = WebTestClient.bindToServer().baseUrl("http://localhost:17173").responseTimeout(Duration.ofSeconds(30)).build();
  private ResponseSpec response;
  private String respString;
  private String billId;

  public void list(String action, String user) {
    respString = new String(client.get().uri(String.format("/bill/%s/%s", user, action)).exchange().returnResult(ResolvableType.forClass(String.class)).getResponseBodyContent());
  }

  public void listEmpty() {
    assertNotNull(respString);
    JSONArray jsonArray = JsonPath.parse(respString).read(JsonPath.compile("$.data"));
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
    bill.setDue(Date.from(LocalDateTime.now().plusSeconds(Integer.valueOf(due))
        .toInstant(ZoneOffset.ofHours(8))));
    bill.setTransferable("");
    response = client.post().uri("/bill").contentType(APPLICATION_JSON_UTF8).body(fromObject(bill)).exchange();
  }

  public void listNotEmpty() {
    assertNotNull(respString);
    JSONArray jsonArray = JsonPath.parse(respString).read(JsonPath.compile("$.data"));
    assertFalse("list should not be empty", jsonArray.isEmpty());
  }

  public void billNotEmpty(String action, String user) {
    byte[] respBytes = client.get().uri("/bill/" + user + "/" + action).exchange().expectStatus().is2xxSuccessful()
        .expectBody().jsonPath("$.data").isNotEmpty().returnResult().getResponseBody();
    respString = new String(respBytes);
    listNotEmpty();
    JSONArray jsonArray = JsonPath.parse(respString).read(JsonPath.compile("$.data"));
    billId = jsonArray.stream().map(o -> (String) ((Map) o).get("id")).findFirst().orElse("");
    assertFalse(billId.isEmpty() || "null".equals(billId));
  }

  public void acceptBill(String user) {
    confirmPresent("acceptance", user, user);
  }

  public void billAccepted(String user) {
    success();
    billList("acceptance", user);
    listEmpty();
  }

  public void success() {
    byte[] respBytes = response.expectStatus().is2xxSuccessful()
        .expectBody().jsonPath("$.data.success").isEqualTo(1)
        .returnResult().getResponseBody();
    respString = new String(respBytes);
  }

  public void guaranteeBill(String user, String as) {
    confirmPresent("guaranty", user, as);
  }

  private void billList(String action, String user) {
    byte[] respBytes = client.get().uri("/bill/" + user + "/" + action).exchange().expectStatus().is2xxSuccessful()
        .expectBody().jsonPath("$.status").isEqualTo(1)
        .returnResult().getResponseBody();
    respString = new String(respBytes);
  }

  public void billInListOf(String action, String user) {
    billList(action, user);
    listNotEmpty();
  }

  private PresentAction actionClass(String actionName, String user) {
    switch (actionName) {
      case "guaranty":
        GuaranteeAction guaranteeAction = new GuaranteeAction();
        guaranteeAction.setClazz(GuaranteeAction.class);
        guaranteeAction.setGuarantor(user);
        return guaranteeAction;
      case "recourse":
        RecourseAction recourseAction = new RecourseAction();
        recourseAction.setClazz(RecourseAction.class);
        recourseAction.setDebtor(user);
        return recourseAction;
      case "endorsement":
        EndorseAction endorseAction = new EndorseAction();
        endorseAction.setClazz(EndorseAction.class);
        endorseAction.setEndorsee(user);
        return endorseAction;
      case "pledge":
        PledgeAction pledgeAction = new PledgeAction();
        pledgeAction.setClazz(PledgeAction.class);
        pledgeAction.setPledgee(user);
        return pledgeAction;
      case "discount":
        DiscountAction discountAction = new DiscountAction();
        discountAction.setClazz(DiscountAction.class);
        discountAction.setReceiver(user);
        return discountAction;
      default:
        break;
    }
    return new PresentAction();
  }

  public void present(String user, String action, String by) throws Exception {
    PresentAction presentAction = actionClass(action, user);
    presentAction.setId(billId);
    presentAction.setManipulator(by);

    if ("payment".equals(action)) {
      TimeUnit.SECONDS.sleep(60);
    } else if ("discount".equals(action)) {
      ((DiscountAction) presentAction).setInterest("0.2%");
      ((DiscountAction) presentAction).setType("0");
      ((DiscountAction) presentAction).setMoney("15");
    }
    ResponseSpec responseSpec = client.post().uri("/bill/" + action).contentType(APPLICATION_JSON_UTF8)
        .body(Mono.just(presentAction), presentAction.getClazz())
        .exchange().expectStatus().is2xxSuccessful();
    if (!"revocation".equals(action)) {
      responseSpec.expectBody().jsonPath("$.data.success").isEqualTo(1);
    }
  }


  public void billGuaranteed(String user) {
    success();
    billList("guaranty", user);
    listEmpty();
  }

  public void receiveBill(String user, String as) {
    confirmPresent("reception", user, as);
  }

  public void billReceived(String user) {
    success();
    billList("reception", user);
    listEmpty();
  }

  public void payBill(String user, String as) {
    confirmPresent("payment", user, as);
  }

  public void billPaid(String user) {
    success();
    billList("payment", user);
    //TODO check bill paid state
  }

  public void rejectBillPayment(String user) {
    rejectPresent("payment", user, user);
  }

  private void rejectPresent(String action, String user, String as) {
    PresentAction presentAction = actionClass(action, user);
    presentAction.setId(billId);
    presentAction.setManipulator(as);
    presentAction.setAction("-1");
    response = client.put().uri("/bill/" + action).contentType(APPLICATION_JSON_UTF8)
        .body(Mono.just(presentAction), presentAction.getClazz()).exchange();
  }


  private void confirmPresent(String action, String user, String as) {
    PresentAction presentAction = actionClass(action, user);
    presentAction.setId(billId);
    presentAction.setManipulator(as);
    presentAction.setAction("1");
    switch (action) {
      case "endorsement":
        ((EndorseAction) presentAction).setEndorsor(user);
        break;
      case "pledge":
        ((PledgeAction) presentAction).setPledger(user);
        break;
      default:
        break;
    }
    response = client.put().uri("/bill/" + action).contentType(APPLICATION_JSON_UTF8)
        .body(Mono.just(presentAction), presentAction.getClazz()).exchange();
  }

  public void billEndorsed(String user, String as) {
    confirmPresent("endorsement", user, as);
  }

  public void pledgeBill(String user, String as) {
    confirmPresent("pledge", user, as);
  }

  public void releasePledge(String user, String as) {
    confirmPresent("pledge/release", user, as);
  }

  public void discountBill(String user, String as) {
    confirmPresent("discount", user, as);
  }

  public void checkDue() {
    response = client.get().uri("/bill/due").exchange();
  }

  public void noBillsDue() {
    response.expectStatus().is2xxSuccessful()
        .expectBody().jsonPath("$.data.payload")
        .isEqualTo("0");
  }

  public void waitForDue(String seconds) throws Exception {
    TimeUnit.SECONDS.sleep(Integer.valueOf(seconds));
  }

  public void billsDue() {
    respString = new String(response.expectStatus().is2xxSuccessful()
        .expectBody().jsonPath("$.data.payload")
        .isNotEmpty().returnResult().getResponseBody());
    listNotEmpty();
  }

}
