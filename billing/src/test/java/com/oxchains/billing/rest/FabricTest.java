package com.oxchains.billing.rest;

import com.jayway.jsonpath.JsonPath;
import com.oxchains.billing.domain.FabricAccount;
import org.junit.Test;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

/**
 * @author aiet
 */
public class FabricTest {

  @Test
  public void testFabricEnrollment() throws Exception {
    String managerPort = "9494";
    String managerHost = "10.8.47.4";
    UriBuilder uriBuilder = new DefaultUriBuilderFactory()
        .builder()
        .scheme("http")
        .host(managerHost)
        .port(managerPort);

    String managerEnrollPath = "/user/token",
        affiliation = "org1",
        password = "111111",
        username = "test";

    ClientResponse fabricEnrollResponse = WebClient.create().post().uri(uriBuilder
        .path(managerEnrollPath).build().toString()
    ).contentType(APPLICATION_JSON_UTF8).body(Mono.just(
        new FabricAccount(username, password, affiliation)), FabricAccount.class
    ).exchange().block(Duration.ofSeconds(10));

    assertTrue(fabricEnrollResponse.statusCode().is2xxSuccessful());

    String responseJson = fabricEnrollResponse.bodyToMono(String.class).block();
    assertNotNull(responseJson);
    String token = JsonPath.parse(responseJson).read(JsonPath.compile("$.data.token"));
    assertNotNull(token);
  }

}
