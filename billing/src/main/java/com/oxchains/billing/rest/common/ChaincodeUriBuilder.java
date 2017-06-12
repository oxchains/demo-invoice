package com.oxchains.billing.rest.common;

import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;

/**
 * @author aiet
 */
public abstract class ChaincodeUriBuilder {


  private final String uriTemplate;

  protected ChaincodeUriBuilder(String uriTemplate) {
    this.uriTemplate = uriTemplate;
  }

  protected URI buildUri(String args) {
    return new DefaultUriBuilderFactory().uriString(uriTemplate).replaceQueryParam("args", args).build();
  }

}
