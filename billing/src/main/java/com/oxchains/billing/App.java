package com.oxchains.billing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

/**
 * @author aiet
 */
@SpringBootApplication
public class App {

  @Value("${fabric.manager.host}")
  private String managerHost;
  @Value("${fabric.manager.port}")
  private String managerPort;

  @Value("${fabric.manager.transaction.path}")
  private String managerTransactionPath;
  @Value("${fabric.chain.name}")
  private String chain;
  @Value("${fabric.chaincode.name}")
  private String chaincode;
  @Value("${fabric.chaincode.version}")
  private String chaincodeVer;

  @Bean
  WebClient webClient() {
    return WebClient.create();
  }

  @Bean({"fabric.uri"})
  @Scope("prototype")
  UriBuilder uriBuilder() {
    return new DefaultUriBuilderFactory()
        .builder()
        .scheme("http")
        .host(managerHost)
        .port(managerPort)
        .path(managerTransactionPath)
        .queryParam("chain", chain)
        .queryParam("chaincode", chaincode)
        .queryParam("version", chaincodeVer);
  }

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

}
