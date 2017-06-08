package com.oxchains.billing;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServletHttpHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import javax.servlet.Servlet;

import static org.springframework.web.reactive.function.server.RouterFunctions.toHttpHandler;

/**
 * @author aiet
 */
@Component
public class RouterServlet extends ServletHttpHandlerAdapter {

  private RouterServlet(RouterFunction<ServerResponse> routerFunction) {
    super(WebHttpHandlerBuilder
      .webHandler(toHttpHandler(routerFunction)).build());
  }

  @Bean
  ServletRegistrationBean servletRegistrationBean() throws Exception {
    ServletRegistrationBean registrationBean = new ServletRegistrationBean<Servlet>(this, "/");
    registrationBean.setLoadOnStartup(1);
    registrationBean.setAsyncSupported(true);
    return registrationBean;
  }

}
