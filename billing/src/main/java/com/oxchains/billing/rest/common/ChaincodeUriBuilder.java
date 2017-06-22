package com.oxchains.billing.rest.common;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;

/**
 * @author aiet
 */
public abstract class ChaincodeUriBuilder {

  private final String uriTemplate;
  protected WebClient client;
  protected String token;

  protected ChaincodeUriBuilder(WebClient client, String token , String uriTemplate) {
    this.client = client;
    this.token = token;
    this.uriTemplate = uriTemplate;
  }

  protected URI buildUri(String args) {
    return new DefaultUriBuilderFactory().uriString(uriTemplate).replaceQueryParam("args", args).build();
  }

}
