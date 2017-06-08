package com.oxchains.billing.rest;

import com.oxchains.billing.domain.Bill;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author aiet
 */
@Component
public class BillHandler {

    /* GET /bill */
    public Mono<ServerResponse> bills(ServerRequest request) {
        //TODO
        return Mono.empty();
    }

    /* POST /bill */
    public Mono<ServerResponse> create(ServerRequest request) {
        Bill billReq;
        //TODO
        return Mono.empty();
    }

    /* GET /bill/{id} */
    public Mono<ServerResponse> bill(ServerRequest request){
        String billId;
        return Mono.empty();
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        return Mono.error(new UnsupportedOperationException());
    }

}
