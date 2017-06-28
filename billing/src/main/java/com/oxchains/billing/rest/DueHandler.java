package com.oxchains.billing.rest;

import com.oxchains.billing.rest.common.ChaincodeUriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import static com.oxchains.billing.App.TOKEN_HOLDER;
import static com.oxchains.billing.domain.BillActions.CHECK_DUE;
import static com.oxchains.billing.rest.common.ClientResponse2ServerResponse.toPayloadTransformedServerResponse;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;

/**
 * @author aiet
 */
@Component
public class DueHandler extends ChaincodeUriBuilder {

  public DueHandler(@Autowired WebClient client,
                    @Autowired @Qualifier("fabric.uri") UriBuilder uriBuilder){
    super(client, uriBuilder.build().toString());
  }

  /* GET /bill/due */
  public Mono<ServerResponse> get(ServerRequest request) {
    return client.get().uri(buildUri(CHECK_DUE)).header(AUTHORIZATION, TOKEN_HOLDER.getToken())
        .accept(APPLICATION_JSON_UTF8).exchange()
        .filter(dueResponse -> dueResponse.statusCode().is2xxSuccessful())
        .flatMap(clientResponse -> Mono.just(toPayloadTransformedServerResponse(clientResponse)))
        .switchIfEmpty(noContent().build());
  }

}
