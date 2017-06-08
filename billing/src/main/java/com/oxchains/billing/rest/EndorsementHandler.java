package com.oxchains.billing.rest;

import com.oxchains.billing.rest.common.EndorseAction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author aiet
 */
@Component
public class EndorsementHandler {

    /* POST /bill/endorsement */
    public Mono<ServerResponse> create(ServerRequest request) {
        EndorseAction action;
        return Mono.empty();
    }

    /* PUT /bill/endorsement */
    public Mono<ServerResponse> update(ServerRequest request) {
        EndorseAction action;
        return Mono.empty();
    }

}
