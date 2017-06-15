package com.oxchains.billing.rest;

import com.oxchains.billing.domain.Bill;
import com.oxchains.billing.rest.common.ChaincodeUriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import static com.oxchains.billing.domain.BillActions.BILL_ISSUE;
import static com.oxchains.billing.domain.BillActions.DELETE;
import static com.oxchains.billing.domain.BillActions.GET;
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
public class BillHandler extends ChaincodeUriBuilder {

  public BillHandler(@Autowired WebClient client,
                     @Autowired @Qualifier("fabric.uri") UriBuilder uriBuilder,
                     @Autowired @Qualifier("token") String token) {
    super(client, token, uriBuilder.build().toString());
  }

  /* GET /bill */
  public Mono<ServerResponse> bills(ServerRequest request) {
    //TODO get bills from all states
    return client.post().uri(buildUri(""))
        .header(AUTHORIZATION, token)
        .accept(APPLICATION_JSON_UTF8).exchange()
        .filter(clientResponse -> clientResponse.statusCode().is2xxSuccessful())
        .flatMap(clientResponse -> Mono.just(toServerResponse(clientResponse)))
        .switchIfEmpty(noContent().build());
  }

  /* POST /bill */
  public Mono<ServerResponse> create(ServerRequest request) {
    return request.bodyToMono(Bill.class)
        .flatMap(bill -> client.post()
            .uri(buildUri(args(BILL_ISSUE, bill)))
            .header(AUTHORIZATION, token)
            .accept(APPLICATION_JSON_UTF8).exchange()
            .filter(clientResponse -> clientResponse.statusCode().is2xxSuccessful())
            .flatMap(clientResponse -> Mono.just(toServerResponse(clientResponse)))
            .switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  /* GET /bill/{id} */
  public Mono<ServerResponse> bill(ServerRequest request) {
    final String billId = request.pathVariable("uid");
    return client.get().uri(buildUri(args(GET, "BillStruct" + billId)))
        .header(AUTHORIZATION, token)
        .accept(APPLICATION_JSON_UTF8).exchange()
        .filter(clientResponse -> clientResponse.statusCode().is2xxSuccessful())
        .flatMap(clientResponse -> Mono.just(toServerResponse(clientResponse)))
        .switchIfEmpty(noContent().build());
  }

  public Mono<ServerResponse> update(ServerRequest request) {
    return Mono.error(new UnsupportedOperationException());
  }


  /* DELETE /bill/{id} */
  public Mono<ServerResponse> del(ServerRequest request) {
    String id = request.pathVariable("id");
    if(!id.startsWith("BillStruct")) id = "BillStruct"+id;
    return client.post().uri(buildUri(args(DELETE, id)))
        .header(AUTHORIZATION, token).accept(APPLICATION_JSON_UTF8).exchange()
        .filter(clientResponse -> clientResponse.statusCode().is2xxSuccessful())
        .flatMap(clientResponse -> Mono.just(toServerResponse(clientResponse)))
        .switchIfEmpty(noContent().build());
  }

}
