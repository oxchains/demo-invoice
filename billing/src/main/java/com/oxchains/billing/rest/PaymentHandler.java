package com.oxchains.billing.rest;

import com.oxchains.billing.notification.PushService;
import com.oxchains.billing.rest.common.ChaincodeUriBuilder;
import com.oxchains.billing.rest.common.ClientResponse2ServerResponse;
import com.oxchains.billing.rest.common.PresentAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static org.springframework.core.ResolvableType.forClass;
import static org.springframework.web.reactive.function.BodyExtractors.toMono;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;

/**
 * @author aiet
 */
@Component
public class PaymentHandler extends ChaincodeUriBuilder {

  private PushService pushService;

  public PaymentHandler(@Autowired WebClient client,
                        @Autowired @Qualifier("fabric.uri") UriBuilder uriBuilder,
                        @Autowired PushService pushService) {
    super(client, uriBuilder.build().toString());
    this.pushService = pushService;
  }

  private final Logger LOG = LoggerFactory.getLogger(getClass());

  /* POST /bill/payment */
  public Mono<ServerResponse> create(ServerRequest request) {
    checkDue().block();
    return request.bodyToMono(PresentAction.class)
        .flatMap(payAction -> chaincodeInvoke(client, buildUri(args(BILL_PAY, payAction)))
            .flatMap(clientResponse -> chaincodeQuery(client, buildUri(args(GET, "BillStruct" + payAction.getId())))
                .flatMap(billResponse ->
                    ((ClientResponse2ServerResponse) toPayloadTransformedServerResponse(billResponse))
                        .billSink().flatMap(bill -> {
                      if (payAction.getAction() == null) {
                        pushService.sendMsg(bill.getDrawee(), "票据到期, 请支付汇票" + payAction.getId());
                      }
                      return Mono.just(toServerResponse(clientResponse));
                    })
                )
            ).switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  private Mono<Object> checkDue() {
    return chaincodeInvoke(client, buildUri(CHECK_DUE))
        .flatMap(clientResponse -> clientResponse.body(toMono(forClass(String.class))))
        .doOnNext(response -> LOG.info("check due response: {}", response));
  }

  /* PUT /bill/payment */
  public Mono<ServerResponse> update(ServerRequest request) {
    checkDue().block();
    return request.bodyToMono(PresentAction.class)
        .flatMap(payAction -> chaincodeInvoke(client, buildUri(args(BILL_PAY, payAction)))
            .flatMap(clientResponse -> chaincodeQuery(client, buildUri(args(GET, "BillStruct" + payAction.getId())))
                .flatMap(billResponse ->
                    ((ClientResponse2ServerResponse) toPayloadTransformedServerResponse(billResponse))
                        .billSink().flatMap(bill -> {
                      if (payAction.getAction() != null) {
                        String action = "1".equals(payAction.getAction()) ? "已支付汇票" : "拒绝支付汇票";
                        pushService.sendMsg(bill.getPayee(), payAction.getManipulator() + action + payAction.getId());
                      }
                      return Mono.just(toServerResponse(clientResponse));
                    })
                )
            ).switchIfEmpty(noContent().build())
        ).switchIfEmpty(badRequest().build());
  }

  public Mono<ServerResponse> get(ServerRequest request) {
    checkDue().block();
    final String uid = request.pathVariable("uid");
    return chaincodeQuery(client, buildUri(args(GET_PAYMENT, uid)))
        .flatMap(clientResponse -> Mono.just(toPayloadTransformedServerResponse(clientResponse)))
        .switchIfEmpty(noContent().build());
  }

}
