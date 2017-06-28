package com.oxchains.billing.rest;

import com.oxchains.billing.notification.PushService;
import com.oxchains.billing.rest.common.ChaincodeUriBuilder;
import com.oxchains.billing.rest.common.EndorseAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import static com.oxchains.billing.domain.BillActions.BILL_ENDORSE;
import static com.oxchains.billing.domain.BillActions.GET_ENDORSEMENT;
import static com.oxchains.billing.rest.common.ClientResponse2ServerResponse.toPayloadTransformedServerResponse;
import static com.oxchains.billing.rest.common.ClientResponse2ServerResponse.toServerResponse;
import static com.oxchains.billing.util.ArgsUtil.args;
import static com.oxchains.billing.util.ResponseUtil.chaincodeInvoke;
import static com.oxchains.billing.util.ResponseUtil.chaincodeQuery;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;

/**
 * @author aiet
 */
@Component
public class EndorsementHandler extends ChaincodeUriBuilder {

  private PushService pushService;

  public EndorsementHandler(@Autowired WebClient client,
                            @Autowired @Qualifier("fabric.uri") UriBuilder uriBuilder,
                            @Autowired PushService pushService) {
    super(client, uriBuilder.build().toString());
    this.pushService = pushService;
  }

  /* POST /bill/endorsement */
  public Mono<ServerResponse> create(ServerRequest request) {
    return request.bodyToMono(EndorseAction.class)
        .flatMap(endorseAction -> chaincodeInvoke(client, buildUri(args(BILL_ENDORSE, endorseAction)))
            .flatMap(clientResponse -> {
              if (endorseAction.getAction() == null) {
                pushService.sendMsg(endorseAction.getEndorsee(), "请确认背书: 汇票" + endorseAction.getId());
              }
              return Mono.just(toServerResponse(clientResponse));
            })
            .switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  /* PUT /bill/endorsement */
  public Mono<ServerResponse> update(ServerRequest request) {
    return request.bodyToMono(EndorseAction.class)
        .flatMap(endorseAction -> chaincodeInvoke(client, buildUri(args(BILL_ENDORSE, endorseAction)))
            .flatMap(clientResponse -> {
              if ("1".equals(endorseAction.getAction())) {
                pushService.sendMsg(endorseAction.getEndorsor(), "背书已确认: 汇票" + endorseAction.getId());
              }
              return Mono.just(toServerResponse(clientResponse));
            })
            .switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  public Mono<ServerResponse> get(ServerRequest request) {
    final String uid = request.pathVariable("uid");
    return chaincodeQuery(client, buildUri(args(GET_ENDORSEMENT, uid)))
        .flatMap(clientResponse -> Mono.just(toPayloadTransformedServerResponse(clientResponse)))
        .switchIfEmpty(noContent().build());
  }

}
