package com.oxchains.billing.rest;

import com.oxchains.billing.rest.common.ChaincodeUriBuilder;
import com.oxchains.billing.rest.common.EndorseAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import static com.oxchains.billing.App.TOKEN_HOLDER;
import static com.oxchains.billing.domain.BillActions.BILL_ENDORSE;
import static com.oxchains.billing.domain.BillActions.GET_ENDORSEMENT;
import static com.oxchains.billing.rest.common.ClientResponse2ServerResponse.toPayloadTransformedServerResponse;
import static com.oxchains.billing.rest.common.ClientResponse2ServerResponse.toServerResponse;
import static com.oxchains.billing.util.ArgsUtil.args;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;

/**
 * @author aiet
 */
@Component
public class EndorsementHandler extends ChaincodeUriBuilder {

  public EndorsementHandler(@Autowired WebClient client,
                            @Autowired @Qualifier("fabric.uri") UriBuilder uriBuilder) {
    super(client, uriBuilder.build().toString());
  }

  /* POST /bill/endorsement */
  public Mono<ServerResponse> create(ServerRequest request) {
    return request.bodyToMono(EndorseAction.class)
        .flatMap(endorseAction -> client.post().uri(buildUri(args(BILL_ENDORSE, endorseAction)))
            .header(AUTHORIZATION, TOKEN_HOLDER.getToken())
            .accept(APPLICATION_JSON_UTF8).exchange()
            .filter(clientResponse -> clientResponse.statusCode().is2xxSuccessful())
            .flatMap(clientResponse -> Mono.just(toServerResponse(clientResponse)))
            .switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  /* PUT /bill/endorsement */
  public Mono<ServerResponse> update(ServerRequest request) {
    return request.bodyToMono(EndorseAction.class)
        .flatMap(endorseAction -> client.post().uri(buildUri(args(BILL_ENDORSE, endorseAction)))
            .header(AUTHORIZATION, TOKEN_HOLDER.getToken())
            .accept(APPLICATION_JSON_UTF8).exchange()
            .filter(clientResponse -> clientResponse.statusCode().is2xxSuccessful())
            .flatMap(clientResponse -> Mono.just(toServerResponse(clientResponse)))
            .switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  public Mono<ServerResponse> get(ServerRequest request) {
    final String uid = request.pathVariable("uid");
    return client.get().uri(buildUri(args(GET_ENDORSEMENT, uid)))
        .header(AUTHORIZATION, TOKEN_HOLDER.getToken())
        .accept(APPLICATION_JSON_UTF8).exchange()
        .filter(clientResponse -> clientResponse.statusCode().is2xxSuccessful())
        .flatMap(clientResponse -> Mono.just(toPayloadTransformedServerResponse(clientResponse)))
        .switchIfEmpty(noContent().build());
  }

}
