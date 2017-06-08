package com.oxchains.billing;

import com.oxchains.billing.rest.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

/**
 * @author aiet
 */
@Configuration
public class AppConfig {

    @Bean
    RouterFunction routerFunction(@Autowired BillHandler bill, @Autowired AcceptanceHandler acceptance, @Autowired WarrantHandler warrant, @Autowired RevocationHandler revocation, @Autowired ReceptionHandler reception, @Autowired DiscountHandler discount,
      @Autowired EndorsementHandler endorsement, @Autowired PaymentHandler payment, @Autowired PledgeHandler pledge) {

        final String billPath = "/bill",
          acceptancePath = "/acceptance",
          discountPath = "/discount",
          endorsePath = "/endorsement",
          paymentPath = "/payment",
          pledgePath = "/pledge",
          pledgeReleasePath = "/release",
          revocationPath = "/revocation",
          warrantPath = "/warrant",
          receptionPath ="/reception";

        return nest(GET(billPath), route(GET("/"), bill::bills).andRoute(GET("/{id}"), bill::bill))
          .andNest(
            POST(billPath), route(POST("/"), bill::create)
            .andRoute(POST(acceptancePath), acceptance::create)
            .andRoute(POST(discountPath), discount::create)
            .andRoute(POST(endorsePath), endorsement::create)
            .andRoute(POST(paymentPath), payment::create)
            .andRoute(POST(receptionPath), reception::create)
            .andRoute(POST(revocationPath), revocation::create)
            .andRoute(POST(warrantPath), warrant::create)
            .andNest(POST(pledgePath), route(POST("/"), pledge::create).andRoute(POST(pledgeReleasePath), pledge::createRelease))
          )
          .andNest(
            PUT(billPath), route(PUT("/"), request -> status(NOT_IMPLEMENTED).build())
            .andRoute(PUT(acceptancePath), acceptance::update)
            .andRoute(PUT(discountPath), discount::update)
            .andRoute(PUT(endorsePath), endorsement::update)
            .andRoute(PUT(paymentPath), payment::update)
            .andRoute(PUT(receptionPath), reception::update)
            .andRoute(PUT(revocationPath), revocation::update)
            .andRoute(PUT(warrantPath), warrant::update)
            .andNest(PUT(pledgePath), route(PUT("/"), pledge::update).andRoute(PUT(pledgeReleasePath), pledge::updateRelease))
          );

    }

}
