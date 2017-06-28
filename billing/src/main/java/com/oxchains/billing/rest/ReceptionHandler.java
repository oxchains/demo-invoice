package com.oxchains.billing.rest;

import com.oxchains.billing.notification.PushService;
import com.oxchains.billing.rest.common.ChaincodeUriBuilder;
import com.oxchains.billing.rest.common.ClientResponse2ServerResponse;
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
public class ReceptionHandler extends ChaincodeUriBuilder {

  private PushService pushService;

  public ReceptionHandler(@Autowired WebClient client,
                          @Autowired @Qualifier("fabric.uri") UriBuilder uriBuilder,
                          @Autowired PushService pushService) {
    super(client, uriBuilder.build().toString());
    this.pushService = pushService;
  }

  /* POST /bill/reception */
  public Mono<ServerResponse> create(ServerRequest request) {
    return request.bodyToMono(PresentAction.class)
        .flatMap(receiveAction -> chaincodeInvoke(client, buildUri(args(BILL_RECEIVE, receiveAction)))
            .flatMap(clientResponse -> chaincodeQuery(client, buildUri(args(GET, "BillStruct" + receiveAction.getId())))
                .flatMap(billResponse ->
                    ((ClientResponse2ServerResponse) toPayloadTransformedServerResponse(billResponse))
                        .billSink().flatMap(bill -> {
                      if (receiveAction.getAction() == null) {
                        pushService.sendMsg(bill.getPayee(), "请即接收汇票" + receiveAction.getId());
                      }
                      return Mono.just(toServerResponse(clientResponse));
                    })
                )
            ).switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  /* PUT /bill/reception */
  public Mono<ServerResponse> update(ServerRequest request) {
    return request.bodyToMono(PresentAction.class)
        .flatMap(receiveAction -> chaincodeInvoke(client, buildUri(args(BILL_RECEIVE, receiveAction)))
            .flatMap(clientResponse -> chaincodeQuery(client, buildUri(args(GET, "BillStruct" + receiveAction.getId())))
                .flatMap(billResponse ->
                    ((ClientResponse2ServerResponse) toPayloadTransformedServerResponse(billResponse))
                        .billSink().flatMap(bill -> {
                      if ("1".equals(receiveAction.getAction())) {
                        pushService.sendMsg(bill.getDrawee(), receiveAction.getManipulator() + "已接收汇票" + receiveAction.getId());
                      }
                      return Mono.just(toServerResponse(clientResponse));
                    })
                )
            ).switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  public Mono<ServerResponse> get(ServerRequest request) {
    final String uid = request.pathVariable("uid");
    return chaincodeQuery(client, buildUri(args(GET_RECEPTION, uid)))
        .flatMap(clientResponse -> Mono.just(toPayloadTransformedServerResponse(clientResponse)))
        .switchIfEmpty(noContent().build());
  }
}
