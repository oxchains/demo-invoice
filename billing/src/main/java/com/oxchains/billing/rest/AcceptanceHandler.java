package com.oxchains.billing.rest;

import com.oxchains.billing.rest.common.PromptAction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author aiet
 */
@Component
public class AcceptanceHandler {

  /* POST /bill/acceptance */
  public Mono<ServerResponse> create(ServerRequest request) {
    PromptAction action;
    return Mono.empty();
  }

  /* PUT /bill/acceptance */
  public Mono<ServerResponse> update(ServerRequest request) {
    PromptAction action;
    return Mono.empty();
  }

}
