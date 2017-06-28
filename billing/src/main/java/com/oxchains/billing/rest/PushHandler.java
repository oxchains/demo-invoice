package com.oxchains.billing.rest;

import com.oxchains.billing.notification.PushService;
import com.oxchains.billing.notification.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * @author aiet
 */
@Component
public class PushHandler {

  private Logger LOG = LoggerFactory.getLogger(getClass());

  private PushService pushService;

  @Autowired
  public PushHandler(PushService pushService) {
    this.pushService = pushService;
  }

  public Mono<ServerResponse> create(ServerRequest request) {
    final String uid = request.pathVariable("uid");
    return request.bodyToMono(Subscription.class).doOnNext(subscription -> {
      pushService.cache(uid, subscription);
    }).flatMap(subscription -> {
      LOG.info("cached subscription of {}: {}", uid, subscription);
      pushService.sendMsg(uid, "server push enabled");
      return ok().build();
    }).switchIfEmpty(ok().build());
  }

}
