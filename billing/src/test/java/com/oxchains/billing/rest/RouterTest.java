package com.oxchains.billing.rest;

import com.oxchains.billing.domain.Bill;
import com.oxchains.billing.rest.common.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Date;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

/**
 * @author aiet
 */
public class RouterTest {

  private WebTestClient client;

  private final String userPath = "/user",
      billPath = "/bill",
      acceptancePath = "/acceptance",
      discountPath = "/discount",
      endorsePath = "/endorsement",
      paymentPath = "/payment",
      pledgePath = "/pledge",
      pledgeReleasePath = "/release",
      revocationPath = "/revocation",
      guarantyPath = "/guaranty",
      receptionPath = "/reception",
      recoursePath = "/recourse";

  @Before
  public void setup() throws Exception {
    client = WebTestClient
        .bindToServer()
        .baseUrl("http://localhost:17173")
        .build();
  }

  @Test
  public void testUser(){
    RegisterAction registerAction = new RegisterAction();
    postEnabled(userPath, registerAction);
    getEnabled(userPath + "/a");
  }

  @Test
  public void testBill() {
    getEnabled(billPath);
    getEnabled(billPath + "/123");
    Bill bill = new Bill();
    bill.setDue(new Date());
    postEnabled(billPath, bill);
  }

  @Test
  public void testAcceptance() {
    PresentAction presentAction = new PresentAction();
    getEnabled(billPath + "/123" + acceptancePath);
    postEnabled(billPath + acceptancePath, presentAction);
    putEnabled(billPath + acceptancePath, presentAction);
  }

  @Test
  public void testDiscount() {
    DiscountAction discountAction = new DiscountAction();
    getEnabled(billPath + "/123" + discountPath);
    postEnabled(billPath + discountPath, discountAction);
    putEnabled(billPath + discountPath, discountAction);
  }

  @Test
  public void testReception() {
    PresentAction presentAction = new PresentAction();
    getEnabled(billPath + "/123" + receptionPath);
    postEnabled(billPath + receptionPath, presentAction);
    putEnabled(billPath + receptionPath, presentAction);
  }

  @Test
  public void testRevocation() {
    PresentAction presentAction = new PresentAction();
    getEnabled(billPath + "/123" + revocationPath);
    postEnabled(billPath + revocationPath, presentAction);
    putEnabled(billPath + revocationPath, presentAction);
  }

  @Test
  public void testEndorsement() {
    EndorseAction endorseAction = new EndorseAction();
    getEnabled(billPath + "/123" + endorsePath);
    postEnabled(billPath + endorsePath, endorseAction);
    putEnabled(billPath + endorsePath, endorseAction);
  }

  @Test
  public void testPayment() {
    PayAction payAction = new PayAction();
    getEnabled(billPath + "/123" + paymentPath);
    postEnabled(billPath + paymentPath, payAction);
    putEnabled(billPath + paymentPath, payAction);
  }

  @Test
  public void testGuaranty() {
    PresentAction presentAction = new PresentAction();
    getEnabled(billPath + "/123" + guarantyPath);
    postEnabled(billPath + guarantyPath, presentAction);
    putEnabled(billPath + guarantyPath, presentAction);
  }

  @Test
  public void testRecourse() {
    RecourseAction recourseAction = new RecourseAction();
    getEnabled(billPath + "/123" + recoursePath);
    postEnabled(billPath + recoursePath, recourseAction);
    putEnabled(billPath + recoursePath, recourseAction);
  }

  @Test
  public void testPledge() {
    PledgeAction pledgeAction = new PledgeAction();
    getEnabled(billPath + "/123" + pledgePath);
    postEnabled(billPath + pledgePath, pledgeAction);
    putEnabled(billPath + pledgePath, pledgeAction);

    PresentAction presentAction = new PresentAction();
    getEnabled(billPath + "/123" + pledgePath + pledgeReleasePath);
    postEnabled(billPath + pledgePath + pledgeReleasePath, presentAction);
    putEnabled(billPath + pledgePath + pledgeReleasePath, presentAction);
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
