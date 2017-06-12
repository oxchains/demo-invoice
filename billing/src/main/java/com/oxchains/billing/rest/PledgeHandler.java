package com.oxchains.billing.rest;

import com.oxchains.billing.rest.common.PledgeAction;
import com.oxchains.billing.rest.common.PromptAction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author aiet
 */
@Component
public class PledgeHandler {

  /* POST /bill/pledge */
  public Mono<ServerResponse> create(ServerRequest request) {
    PledgeAction action;
    return Mono.empty();
  }

  /* PUT /bill/pledge */
  public Mono<ServerResponse> update(ServerRequest request) {
    PledgeAction action;
    return Mono.empty();
  }

  /* POST /bill/pledge/release */
  public Mono<ServerResponse> createRelease(ServerRequest request) {
    PromptAction action;
    return Mono.empty();
  }

  /* PUT /bill/pledge/release */
  public Mono<ServerResponse> updateRelease(ServerRequest request) {
    PromptAction action;
    return Mono.empty();
  }

}
