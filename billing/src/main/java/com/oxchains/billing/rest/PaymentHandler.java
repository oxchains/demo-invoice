package com.oxchains.billing.rest;

import com.oxchains.billing.rest.common.PayAction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author aiet
 */
@Component
public class PaymentHandler {

    /* POST /bill/payment */
    public Mono<ServerResponse> create(ServerRequest request) {
        PayAction action;
        return Mono.empty();
    }

    /* PUT /bill/payment */
    public Mono<ServerResponse> update(ServerRequest request) {
        PayAction action;
        return Mono.empty();
    }

}
