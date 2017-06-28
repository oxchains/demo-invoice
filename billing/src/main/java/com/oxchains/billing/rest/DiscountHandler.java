package com.oxchains.billing.rest;

import com.oxchains.billing.notification.PushService;
import com.oxchains.billing.rest.common.ChaincodeUriBuilder;
import com.oxchains.billing.rest.common.ClientResponse2ServerResponse;
import com.oxchains.billing.rest.common.DiscountAction;
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
public class DiscountHandler extends ChaincodeUriBuilder {

  private PushService pushService;

  public DiscountHandler(@Autowired WebClient client,
                         @Autowired @Qualifier("fabric.uri") UriBuilder uriBuilder,
                         @Autowired PushService pushService) {
    super(client, uriBuilder.build().toString());
    this.pushService = pushService;
  }

  /* POST /bill/discount */
  public Mono<ServerResponse> create(ServerRequest request) {
    return request.bodyToMono(DiscountAction.class)
        .flatMap(discountAction -> chaincodeInvoke(client, buildUri(args(BILL_DISCOUNT, discountAction)))
            .flatMap(clientResponse -> {
              if (discountAction.getInterest() == null) {
                pushService.sendMsg(discountAction.getReceiver(), "请即贴现: 汇票" + discountAction.getId());
              }
              return Mono.just(toServerResponse(clientResponse));
            })
            .switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  /* PUT /bill/discount */
  public Mono<ServerResponse> update(ServerRequest request) {
    return request.bodyToMono(DiscountAction.class)
        .flatMap(discountAction -> chaincodeInvoke(client, buildUri(args(BILL_DISCOUNT, discountAction)))
            .flatMap(clientResponse -> chaincodeQuery(client, buildUri(args(GET, "BillStruct" + discountAction.getId())))
                .flatMap(billResponse ->
                    ((ClientResponse2ServerResponse) toPayloadTransformedServerResponse(billResponse))
                        .billSink().flatMap(bill -> {
                      if ("1".equals(discountAction.getAction())) {
                        pushService.sendMsg(bill.getPayee(), "已确认贴现: 汇票" + discountAction.getId());
                      }
                      return Mono.just(toServerResponse(clientResponse));
                    })
                )
            ).switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  public Mono<ServerResponse> get(ServerRequest request) {
    final String uid = request.pathVariable("uid");
    return chaincodeQuery(client, buildUri(args(GET_DISCOUNT, uid)))
        .flatMap(clientResponse -> Mono.just(toPayloadTransformedServerResponse(clientResponse)))
        .switchIfEmpty(noContent().build());
  }

}
