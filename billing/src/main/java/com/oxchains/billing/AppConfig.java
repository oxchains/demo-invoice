package com.oxchains.billing;

import com.oxchains.billing.rest.PushHandler;
import com.oxchains.billing.rest.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse.BodyBuilder;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

/**
 * @author aiet
 */
@Configuration
public class AppConfig {

  @Bean
  RouterFunction routerFunction(
      @Autowired UserHandler user, @Autowired BillHandler bill, @Autowired PushHandler subscription,
      @Autowired AcceptanceHandler acceptance, @Autowired GuarantyHandler warrant,
      @Autowired RevocationHandler revocation, @Autowired ReceptionHandler reception,
      @Autowired DiscountHandler discount, @Autowired EndorsementHandler endorsement,
      @Autowired PaymentHandler payment, @Autowired PledgeHandler pledge,
      @Autowired RecourseHandler recourse, @Autowired DueHandler due) {

    final String userPath = "/user", subscriptionPath = "/subscription";
    final String billPath = "/bill",
        acceptancePath = "/acceptance",
        discountPath = "/discount",
        endorsePath = "/endorsement",
        paymentPath = "/payment",
        pledgePath = "/pledge",
        pledgeReleasePath = "/release",
        revocationPath = "/revocation",
        guarantyPath = "/guaranty",
        receptionPath = "/reception",
        recoursePath = "/recourse",
        duePath = "/due";

    return route(OPTIONS("/**"), request -> {
      BodyBuilder bodyBuilder = ok();
      request.headers().header(ORIGIN).forEach(origin ->
          bodyBuilder.header(ACCESS_CONTROL_ALLOW_ORIGIN, origin));
      request.headers().header(ACCESS_CONTROL_REQUEST_HEADERS).forEach(header ->
          bodyBuilder.header(ACCESS_CONTROL_ALLOW_HEADERS, header));
      request.headers().header(ACCESS_CONTROL_REQUEST_METHOD).forEach(method ->
          bodyBuilder.header(ACCESS_CONTROL_ALLOW_METHODS, method));
      return bodyBuilder.build();
    }).andRoute(POST(userPath), user::register).andRoute(GET(userPath + "/{uid}"), user::get)
        .andNest(
            GET(billPath),
            route(GET("/"), bill::bills).andRoute(GET(duePath), due::get).andNest(GET("/{uid}"),
                route(GET("/"), bill::bill)
                    .andRoute(GET(acceptancePath), acceptance::get)
                    .andRoute(GET(discountPath), discount::get)
                    .andRoute(GET(endorsePath), endorsement::get)
                    .andRoute(GET(paymentPath), payment::get)
                    .andRoute(GET(receptionPath), reception::get)
                    .andRoute(GET(revocationPath), revocation::get)
                    .andRoute(GET(guarantyPath), warrant::get)
                    .andRoute(GET(recoursePath), recourse::get)
                    .andNest(GET(pledgePath), route(GET("/"), pledge::get)
                        .andRoute(GET(pledgeReleasePath), pledge::getRelease))
            )
        ).andNest(
            POST(billPath), route(POST("/"), bill::create)
                .andRoute(POST("/{uid}" + subscriptionPath), subscription::create)
                .andRoute(POST(acceptancePath), acceptance::create)
                .andRoute(POST(discountPath), discount::create)
                .andRoute(POST(endorsePath), endorsement::create)
                .andRoute(POST(paymentPath), payment::create)
                .andRoute(POST(receptionPath), reception::create)
                .andRoute(POST(revocationPath), revocation::create)
                .andRoute(POST(guarantyPath), warrant::create)
                .andRoute(POST(recoursePath), recourse::create)
                .andNest(POST(pledgePath), route(POST("/"), pledge::create)
                    .andRoute(POST(pledgeReleasePath), pledge::createRelease))
        ).andNest(
            PUT(billPath), route(PUT("/"), request -> status(NOT_IMPLEMENTED).build())
                .andRoute(PUT(acceptancePath), acceptance::update)
                .andRoute(PUT(discountPath), discount::update)
                .andRoute(PUT(endorsePath), endorsement::update)
                .andRoute(PUT(paymentPath), payment::update)
                .andRoute(PUT(receptionPath), reception::update)
                .andRoute(PUT(guarantyPath), warrant::update)
                .andRoute(PUT(recoursePath), recourse::update)
                .andNest(PUT(pledgePath), route(PUT("/"), pledge::update)
                    .andRoute(PUT(pledgeReleasePath), pledge::updateRelease))
        ).andRoute(DELETE(billPath + "/{id}"), bill::del)
        .andRoute(GET("/peer"), request -> ok().build());

  }

}
