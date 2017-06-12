package com.oxchains.billing.rest;

import com.oxchains.billing.rest.common.RecourseAction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author aiet
 */
@Component
public class RecourseHandler {

  /* POST /bill/recourse */
  public Mono<ServerResponse> create(ServerRequest request) {
    RecourseAction action;
    return Mono.empty();
  }

  /* PUT /bill/recourse */
  public Mono<ServerResponse> update(ServerRequest request) {
    RecourseAction action;
    return Mono.empty();
  }
}
