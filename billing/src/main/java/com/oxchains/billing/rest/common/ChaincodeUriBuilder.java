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

  protected ChaincodeUriBuilder(WebClient client, String uriTemplate) {
    this.client = client;
    this.uriTemplate = uriTemplate;
  }

  public URI buildUri(String args) {
    return new DefaultUriBuilderFactory().uriString(uriTemplate).replaceQueryParam("args", args).build();
  }

}
