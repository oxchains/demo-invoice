package com.oxchains.billing.rest;

import com.oxchains.billing.domain.Bill;
import com.oxchains.billing.rest.common.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

/**
 * @author aiet
 */
public class RouterTest {

  private static WebTestClient client;

  private final String billPath = "/bill", acceptancePath = "/acceptance", discountPath = "/discount", endorsePath = "/endorsement", paymentPath = "/payment", pledgePath = "/pledge", pledgeReleasePath = "/release", revocationPath = "/revocation", warrantPath = "/warrant", receptionPath = "/reception", recoursePath = "/recourse";

  @Before
  public void setup() throws Exception {
    client = WebTestClient
        .bindToServer()
        .baseUrl("http://localhost:8080")
        .build();
  }

  @Test
  public void testBill() {
    getEnabled(billPath);
    getEnabled(billPath + "/123");
    Bill bill = new Bill();
    postEnabled(billPath, bill);
  }

  @Test
  public void testAcceptance() {
    PromptAction promptAction = new PromptAction();
    postEnabled(billPath + acceptancePath, promptAction);
    putEnabled(billPath + acceptancePath, promptAction);
  }

  @Test
  public void testDiscount() {
    DiscountAction discountAction = new DiscountAction();
    postEnabled(billPath + discountPath, discountAction);
    putEnabled(billPath + discountPath, discountAction);
  }

  @Test
  public void testReception() {
    PromptAction promptAction = new PromptAction();
    postEnabled(billPath + receptionPath, promptAction);
    putEnabled(billPath + receptionPath, promptAction);
  }

  @Test
  public void testRevocation() {
    PromptAction promptAction = new PromptAction();
    postEnabled(billPath + revocationPath, promptAction);
    putEnabled(billPath + revocationPath, promptAction);
  }

  @Test
  public void testEndorsement() {
    EndorseAction endorseAction = new EndorseAction();
    postEnabled(billPath + endorsePath, endorseAction);
    putEnabled(billPath + endorsePath, endorseAction);
  }

  @Test
  public void testPayment() {
    PayAction payAction = new PayAction();
    postEnabled(billPath + paymentPath, payAction);
    putEnabled(billPath + paymentPath, payAction);
  }

  @Test
  public void testWarrant() {
    PromptAction promptAction = new PromptAction();
    postEnabled(billPath + warrantPath, promptAction);
    putEnabled(billPath + warrantPath, promptAction);
  }

  @Test
  public void testRecourse() {
    RecourseAction recourseAction = new RecourseAction();
    postEnabled(billPath + recoursePath, recourseAction);
    putEnabled(billPath + recoursePath, recourseAction);
  }

  @Test
  public void testPledge() {
    PledgeAction pledgeAction = new PledgeAction();
    postEnabled(billPath + pledgePath, pledgeAction);
    putEnabled(billPath + pledgePath, pledgeAction);

    PromptAction promptAction = new PromptAction();
    postEnabled(billPath + pledgePath + pledgeReleasePath, promptAction);
    putEnabled(billPath + pledgePath + pledgeReleasePath, promptAction);
  }

  private void getEnabled(String path) {
    client.get().uri(path).exchange().expectStatus().is2xxSuccessful();
  }

  private void postEnabled(String path) {
    client.post().uri(path).contentType(APPLICATION_JSON_UTF8).exchange().expectStatus().is2xxSuccessful();
  }

  private void postEnabled(String path, Object object) {
    client.post().uri(path).contentType(APPLICATION_JSON_UTF8).body(fromObject(object)).exchange().expectStatus().is2xxSuccessful();
  }

  private void putEnabled(String path) {
    client.put().uri(path).exchange().expectStatus().is2xxSuccessful();
  }

  private void putEnabled(String path, Object object) {
    client.put().uri(path).contentType(APPLICATION_JSON_UTF8).body(fromObject(object)).exchange().expectStatus().is2xxSuccessful();
  }

}
