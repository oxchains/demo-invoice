package com.oxchains.billing.rest;

import com.oxchains.billing.rest.common.ChaincodeUriBuilder;
import com.oxchains.billing.rest.common.PresentAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import static com.oxchains.billing.App.TOKEN_HOLDER;
import static com.oxchains.billing.domain.BillActions.*;
import static com.oxchains.billing.rest.common.ClientResponse2ServerResponse.toPayloadTransformedServerResponse;
import static com.oxchains.billing.rest.common.ClientResponse2ServerResponse.toServerResponse;
import static com.oxchains.billing.util.ArgsUtil.args;
import static org.springframework.core.ResolvableType.forClass;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.BodyExtractors.toMono;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;

/**
 * @author aiet
 */
@Component
public class PaymentHandler extends ChaincodeUriBuilder {

  public PaymentHandler(@Autowired WebClient client,
                        @Autowired @Qualifier("fabric.uri") UriBuilder uriBuilder) {
    super(client, uriBuilder.build().toString());
  }

  private final Logger LOG = LoggerFactory.getLogger(getClass());

  /* POST /bill/payment */
  public Mono<ServerResponse> create(ServerRequest request) {
    checkDue().block();
    return request.bodyToMono(PresentAction.class)
        .flatMap(payAction -> client.post().uri(buildUri(args(BILL_PAY, payAction)))
            .header(AUTHORIZATION, TOKEN_HOLDER.getToken())
            .accept(APPLICATION_JSON_UTF8).exchange()
            .filter(clientResponse -> clientResponse.statusCode().is2xxSuccessful())
            .flatMap(clientResponse -> Mono.just(toServerResponse(clientResponse)))
        ).switchIfEmpty(noContent().build());
  }

  private Mono<Object> checkDue() {
    return client.post().uri(buildUri(CHECK_DUE)).header(AUTHORIZATION, TOKEN_HOLDER.getToken())
        .accept(APPLICATION_JSON_UTF8).exchange()
        .filter(dueResponse -> dueResponse.statusCode().is2xxSuccessful())
        .flatMap(clientResponse -> clientResponse.body(toMono(forClass(String.class))))
        .doOnNext(response -> LOG.info("check due response: {}", response));
  }

  /* PUT /bill/payment */
  public Mono<ServerResponse> update(ServerRequest request) {
    checkDue().block();
    return request.bodyToMono(PresentAction.class)
        .flatMap(payAction -> client.post().uri(buildUri(args(BILL_PAY, payAction)))
            .header(AUTHORIZATION, TOKEN_HOLDER.getToken())
            .accept(APPLICATION_JSON_UTF8).exchange()
            .filter(clientResponse -> clientResponse.statusCode().is2xxSuccessful())
            .flatMap(clientResponse -> Mono.just(toServerResponse(clientResponse)))
            .switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  public Mono<ServerResponse> get(ServerRequest request) {
    checkDue().block();
    final String uid = request.pathVariable("uid");
    return client.get().uri(buildUri(args(GET_PAYMENT, uid)))
        .header(AUTHORIZATION, TOKEN_HOLDER.getToken())
        .accept(APPLICATION_JSON_UTF8).exchange()
        .filter(clientResponse -> clientResponse.statusCode().is2xxSuccessful())
        .flatMap(clientResponse -> Mono.just(toPayloadTransformedServerResponse(clientResponse)))
        .switchIfEmpty(noContent().build());
  }

}
