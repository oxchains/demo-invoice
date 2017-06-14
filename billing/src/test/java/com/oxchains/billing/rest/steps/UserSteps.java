package com.oxchains.billing.rest.steps;

import com.oxchains.billing.rest.common.RegisterAction;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

/**
 * @author aiet
 */
public class UserSteps {

  private WebTestClient client = WebTestClient.bindToServer().baseUrl("http://localhost:17173").build();
  private ResponseSpec responseSpec;
  private String newUser;

  public void hasUser(String... users) {
    for (String u : users) {
      userFound(u);
    }
  }

  public void register(){
    register(newUser);
  }

  public void register(String user) {
    newUser = user;
    RegisterAction registerAction = new RegisterAction();
    registerAction.setUser(user);
    registerAction.setAsset(RandomStringUtils.random(3, false, true));
    responseSpec = client.post().uri("/user").contentType(APPLICATION_JSON_UTF8)
        .body(Mono.just(registerAction), RegisterAction.class).exchange();
  }

  public void registrationFail() {
    responseSpec.expectBody().jsonPath("status").isEqualTo(-1);
  }

  public void userNotFound(){
    newUser = RandomStringUtils.random(5, true, true);
    notExistsUser(newUser);
  }

  public void userNotFound(String user) {
    notExistsUser(user);
  }

  public void userFound(){
    existsUser(newUser);
  }

  public void userFound(String user) {
    existsUser(user);
  }

  private void existsUser(String user) {
    client.get().uri("/user/" + user).exchange().expectStatus().is2xxSuccessful()
        .expectBody().jsonPath("data.payload").isNotEmpty();
  }

  private void notExistsUser(String user) {
    client.get().uri("/user/" + user).exchange().expectStatus().is2xxSuccessful()
        .expectBody().jsonPath("status").isEqualTo(-1);
  }


  public void delete(String user) {
    client.delete().uri("/user/"+user).exchange().expectStatus().is2xxSuccessful()
        .expectBody().jsonPath("status").isEqualTo(1);
  }

}
