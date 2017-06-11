package com.oxchains.billing.rest;

import com.oxchains.billing.domain.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * @author aiet
 */
@Component
public class BillHandler {

    private final WebClient client;
    private final UriBuilder uriBuilder;

    public BillHandler(@Autowired WebClient client, @Autowired @Qualifier("fabric.uri") UriBuilder uriBuilder) {
        this.client = client;
        this.uriBuilder = uriBuilder;
    }

    /* GET /bill */
    public Mono<ServerResponse> bills(ServerRequest request) {
        //TODO
        return client
          .post()
          .uri(uriBuilder.queryParam("args", "").build())
          .accept(APPLICATION_JSON_UTF8)
          .exchange()
          .filter(clientResponse -> clientResponse.statusCode().is2xxSuccessful())
          .flatMap(clientResponse -> {
              //convert client response to server response
              return ok().build();
          }).switchIfEmpty(noContent().build());
    }

    /* POST /bill */
    public Mono<ServerResponse> create(ServerRequest request) {
        Bill billReq;
        //TODO
        return Mono.empty();
    }

    /* GET /bill/{id} */
    public Mono<ServerResponse> bill(ServerRequest request) {
        String billId;
        return Mono.empty();
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        return Mono.error(new UnsupportedOperationException());
    }

}
