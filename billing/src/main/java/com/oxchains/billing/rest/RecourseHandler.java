package com.oxchains.billing.rest;

import com.oxchains.billing.notification.PushService;
import com.oxchains.billing.rest.common.ChaincodeUriBuilder;
import com.oxchains.billing.rest.common.RecourseAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import static com.oxchains.billing.domain.BillActions.BILL_RECOURSE;
import static com.oxchains.billing.domain.BillActions.GET_RECOURSE;
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
public class RecourseHandler extends ChaincodeUriBuilder {

  private PushService pushService;

  public RecourseHandler(@Autowired WebClient client,
                         @Autowired @Qualifier("fabric.uri") UriBuilder uriBuilder,
                         @Autowired PushService pushService) {
    super(client, uriBuilder.build().toString());
    this.pushService = pushService;
  }


  /* POST /bill/recourse */
  public Mono<ServerResponse> create(ServerRequest request) {
    return request.bodyToMono(RecourseAction.class)
        .flatMap(recourseAction -> chaincodeInvoke(client, buildUri(args(BILL_RECOURSE, recourseAction)))
            .flatMap(clientResponse -> {
              if (recourseAction.getAction() == null) {
                pushService.sendMsg(recourseAction.getDebtor(), "票据到期, 请支付汇票" + recourseAction.getId());
              }
              return Mono.just(toServerResponse(clientResponse));
            })
            .switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  /* PUT /bill/recourse */
  public Mono<ServerResponse> update(ServerRequest request) {
    return request.bodyToMono(RecourseAction.class)
        .flatMap(recourseAction -> chaincodeInvoke(client, buildUri(args(BILL_RECOURSE, recourseAction)))
            .flatMap(clientResponse -> Mono.just(toServerResponse(clientResponse)))
            .switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  public Mono<ServerResponse> get(ServerRequest request) {
    final String uid = request.pathVariable("uid");
    return chaincodeQuery(client, buildUri(args(GET_RECOURSE, uid)))
        .flatMap(clientResponse -> Mono.just(toPayloadTransformedServerResponse(clientResponse)))
        .switchIfEmpty(noContent().build());
  }

}
