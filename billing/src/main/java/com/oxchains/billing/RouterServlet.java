package com.oxchains.billing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServletHttpHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.core.publisher.Mono;

import javax.servlet.Servlet;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.reactive.function.server.RouterFunctions.toHttpHandler;

/**
 * @author aiet
 */
@Component
public class RouterServlet extends ServletHttpHandlerAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(RouterServlet.class);

  private RouterServlet(RouterFunction<ServerResponse> routerFunction) {
    super(WebHttpHandlerBuilder
        .webHandler(toHttpHandler(routerFunction)).prependFilter((exchange, chain) -> {
          exchange.getResponse().getHeaders().add(ACCESS_CONTROL_ALLOW_ORIGIN,
              exchange.getRequest().getHeaders().getOrigin());
          return chain.filter(exchange);
        }).prependExceptionHandler((exchange, ex) -> {
          exchange.getResponse().setStatusCode(BAD_REQUEST);
          LOG.error("failed to handle request {} with {}:",
              exchange.getRequest().getRemoteAddress(), exchange.getRequest().getQueryParams(),
              ex);
          return Mono.empty();
        }).build());
  }

  @Bean
  ServletRegistrationBean servletRegistrationBean() throws Exception {
    ServletRegistrationBean registrationBean = new ServletRegistrationBean<Servlet>(this, "/");
    registrationBean.setLoadOnStartup(1);
    registrationBean.setAsyncSupported(true);
    return registrationBean;
  }

}
