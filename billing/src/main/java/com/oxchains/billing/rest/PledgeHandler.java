package com.oxchains.billing.rest;

import com.oxchains.billing.notification.PushService;
import com.oxchains.billing.rest.common.ChaincodeUriBuilder;
import com.oxchains.billing.rest.common.PledgeAction;
import com.oxchains.billing.rest.common.PresentAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import static com.oxchains.billing.domain.BillActions.*;
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
public class PledgeHandler extends ChaincodeUriBuilder {

  private PushService pushService;

  public PledgeHandler(@Autowired WebClient client,
                       @Autowired @Qualifier("fabric.uri") UriBuilder uriBuilder,
                       @Autowired PushService pushService) {
    super(client, uriBuilder.build().toString());
    this.pushService = pushService;
  }


  /* POST /bill/pledge */
  public Mono<ServerResponse> create(ServerRequest request) {
    return request.bodyToMono(PledgeAction.class)
        .flatMap(pledgeAction -> chaincodeInvoke(client, buildUri(args(BILL_PLEDGE, pledgeAction)))
            .flatMap(clientResponse -> {
              if (pledgeAction.getAction() == null) {
                pushService.sendMsg(pledgeAction.getPledgee(), "请确认质押汇票" + pledgeAction.getId());
              }
              return Mono.just(toServerResponse(clientResponse));
            })
            .switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  /* PUT /bill/pledge */
  public Mono<ServerResponse> update(ServerRequest request) {
    return request.bodyToMono(PledgeAction.class)
        .flatMap(pledgeAction -> chaincodeInvoke(client, buildUri(args(BILL_PLEDGE, pledgeAction)))
            .flatMap(clientResponse -> {
              if ("1".equals(pledgeAction.getAction())) {
                pushService.sendMsg(pledgeAction.getPledger(), "已确认质押: 汇票" + pledgeAction.getId());
              }
              return Mono.just(toServerResponse(clientResponse));
            })
            .switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  /* POST /bill/pledge/release */
  public Mono<ServerResponse> createRelease(ServerRequest request) {
    return request.bodyToMono(PresentAction.class)
        .flatMap(pledgeReleaseAction -> chaincodeInvoke(client, buildUri(args(BILL_RELEASE_PLEDGE, pledgeReleaseAction)))
            .flatMap(clientResponse -> Mono.just(toServerResponse(clientResponse)))
            .switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  /* PUT /bill/pledge/release */
  public Mono<ServerResponse> updateRelease(ServerRequest request) {
    return request.bodyToMono(PresentAction.class)
        .flatMap(pledgeReleaseAction -> chaincodeInvoke(client, buildUri(args(BILL_RELEASE_PLEDGE, pledgeReleaseAction)))
            .flatMap(clientResponse -> Mono.just(toServerResponse(clientResponse)))
            .switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  public Mono<ServerResponse> get(ServerRequest request) {
    final String billId = request.pathVariable("uid");
    return chaincodeQuery(client, buildUri(args(GET_PLEDGE, billId)))
        .flatMap(clientResponse -> Mono.just(toPayloadTransformedServerResponse(clientResponse)))
        .switchIfEmpty(noContent().build());
  }

  public Mono<ServerResponse> getRelease(ServerRequest request) {
    final String uid = request.pathVariable("uid");
    return chaincodeQuery(client, buildUri(args(GET_PLEDGE_RELEASE, uid)))
        .flatMap(clientResponse -> Mono.just(toPayloadTransformedServerResponse(clientResponse)))
        .switchIfEmpty(noContent().build());
  }

}
