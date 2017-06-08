package com.oxchains.billing.rest;

import com.oxchains.billing.App;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author aiet
 */
@SpringBootTest
@ContextConfiguration(classes = App.class)
public class RouterTest {

    private static WebTestClient client;

    final String billPath = "/bill", acceptancePath = "/acceptance", discountPath = "/discount", endorsePath = "/endorsement", paymentPath = "/payment", pledgePath = "/pledge", pledgeReleasePath = "/release", revocationPath = "/revocation", warrantPath = "/warrant", receptionPath = "/reception";

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
        postEnabled(billPath);
        //client.put().uri(billPath).exchange().expectStatus().is5xxServerError();
    }

    @Test
    public void testAcceptance() {
        postEnabled(billPath + acceptancePath);
        putEnabled(billPath + acceptancePath);
    }

    @Test
    public void testDiscount() {
        postEnabled(billPath + discountPath);
        putEnabled(billPath + discountPath);
    }

    @Test
    public void testReception() {
        postEnabled(billPath + receptionPath);
        putEnabled(billPath + receptionPath);
    }

    @Test
    public void testRevocation() {
        postEnabled(billPath + revocationPath);
        putEnabled(billPath + revocationPath);
    }

    @Test
    public void testEndorsement(){
        postEnabled(billPath + endorsePath);
        putEnabled(billPath + endorsePath);
    }

    @Test
    public void testPayment(){
        postEnabled(billPath + paymentPath);
        putEnabled(billPath + paymentPath);
    }

    @Test
    public void testWarrant(){
        postEnabled(billPath + warrantPath);
        putEnabled(billPath + warrantPath);
    }

    @Test
    public void testPledge(){
        postEnabled(billPath + pledgePath);
        putEnabled(billPath + pledgePath);

        postEnabled(billPath + pledgePath + pledgeReleasePath);
        putEnabled(billPath + pledgePath + pledgeReleasePath);
    }

    private void getEnabled(String path){
        client.get().uri(path).exchange().expectStatus().is2xxSuccessful();
    }

    private void postEnabled(String path){
        client.post().uri(path).exchange().expectStatus().is2xxSuccessful();
    }

    private void putEnabled(String path){
        client.put().uri(path).exchange().expectStatus().is2xxSuccessful();
    }

}
