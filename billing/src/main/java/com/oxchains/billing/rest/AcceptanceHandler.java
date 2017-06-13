package com.oxchains.billing.rest;

import com.oxchains.billing.rest.common.ChaincodeUriBuilder;
import com.oxchains.billing.rest.common.PromptAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import static com.oxchains.billing.domain.BillActions.BILL_ACCEPT;
import static com.oxchains.billing.domain.BillActions.BILL_NEW;
import static com.oxchains.billing.util.ArgsUtil.args;
import static com.oxchains.billing.rest.common.ClientResponse2ServerResponse.toServerResponse;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;

/**
 * @author aiet
 */
@Component
public class AcceptanceHandler extends ChaincodeUriBuilder {

  private final WebClient client;

  protected AcceptanceHandler(@Autowired WebClient client, @Autowired @Qualifier("fabric.uri") UriBuilder uriBuilder) {
    super(uriBuilder.build().toString());
    this.client = client;
  }

  /* POST /bill/acceptance */
  public Mono<ServerResponse> create(ServerRequest request) {
    return request.bodyToMono(PromptAction.class)
        .flatMap(promptAction -> client.post()
            .uri(buildUri(args(BILL_NEW, promptAction)))
            .accept(APPLICATION_JSON_UTF8).exchange()
            .filter(clientResponse -> clientResponse.statusCode().is2xxSuccessful())
            .flatMap(clientResponse -> Mono.just(toServerResponse(clientResponse)))
            .switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  /* PUT /bill/acceptance */
  public Mono<ServerResponse> update(ServerRequest request) {
    return request.bodyToMono(PromptAction.class)
        .flatMap(promptAction -> client.post()
            .uri(buildUri(args(BILL_ACCEPT, promptAction)))
            .accept(APPLICATION_JSON_UTF8).exchange()
            .filter(clientResponse -> clientResponse.statusCode().is2xxSuccessful())
            .flatMap(clientResponse -> Mono.just(toServerResponse(clientResponse)))
            .switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

}
