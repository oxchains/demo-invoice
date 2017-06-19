package com.oxchains.billing.notification;

import com.google.common.cache.Cache;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
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

  private Cache<String, Subscription> cache;

  @Autowired
  public PushHandler(Cache<String, Subscription> cache) {
    this.cache = cache;
  }

  public Mono<ServerResponse> create(ServerRequest request) {
    final String uid = request.pathVariable("uid");
    return request.bodyToMono(Subscription.class).doOnNext(subscription -> {
      cache.put(uid, subscription);
    }).flatMap(subscription-> {
      LOG.info("cached subscription of {}: {}", uid, subscription);
      try {
      /* send a test push notification */
        Notification notification = new Notification(
            subscription.getEndpoint(),
            subscription.getPublicKey(),
            subscription.getAuthAsBytes(),
            "test".getBytes()
        );
        new PushService().send(notification);
      }catch (Exception e){
        LOG.error("failed to send test notification to {}", uid);
      }
      return ok().build();
    }).switchIfEmpty(ok().build());
  }

}
