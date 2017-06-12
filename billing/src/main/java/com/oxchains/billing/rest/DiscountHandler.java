package com.oxchains.billing.rest;

import com.oxchains.billing.rest.common.DiscountAction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author aiet
 */
@Component
public class DiscountHandler {

  /* POST /bill/discount */
  public Mono<ServerResponse> create(ServerRequest request) {
    DiscountAction action;
    return Mono.empty();
  }

  /* PUT /bill/discount */
  public Mono<ServerResponse> update(ServerRequest request) {
    DiscountAction action;
    return Mono.empty();
  }
}
