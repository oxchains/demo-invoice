package com.oxchains.billing;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.oxchains.billing.domain.FabricAccount;
import com.oxchains.billing.notification.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.security.Security;
import java.time.Duration;
import java.util.Optional;

import static com.oxchains.billing.util.ResponseUtil.extract;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

/**
 * @author aiet
 */
@SpringBootApplication
public class App {

  private Logger LOG = LoggerFactory.getLogger(getClass());

  @Value("${fabric.manager.host}")
  private String managerHost;
  @Value("${fabric.manager.port}")
  private String managerPort;

  @Value("${fabric.manager.transaction.path}")
  private String managerTransactionPath;
  @Value("${fabric.manager.enroll.path}")
  private String managerEnrollPath;

  @Value("${fabric.chain.name}")
  private String chain;
  @Value("${fabric.chaincode.name}")
  private String chaincode;
  @Value("${fabric.chaincode.version}")
  private String chaincodeVer;

  @Value("${fabric.username}")
  private String username;
  @Value("${fabric.password}")
  private String password;
  @Value("${fabric.affiliation}")
  private String affiliation;

  @Bean
  WebClient webClient() {
    return WebClient.create();
  }

  @Bean({"fabric.uri"})
  UriBuilder uriBuilder() {
    UriBuilder uriBuilder = new DefaultUriBuilderFactory()
        .builder()
        .scheme("http")
        .host(managerHost)
        .port(managerPort);

    return uriBuilder
        .path(managerTransactionPath)
        .queryParam("chain", chain)
        .queryParam("chaincode", chaincode)
        .queryParam("version", chaincodeVer)
        .queryParam("args", "");
  }

  @Bean({"token"})
  String token(@Autowired @Qualifier("fabric.uri") UriBuilder uriBuilder) {
    String json = (String) WebClient.create().post().uri(new DefaultUriBuilderFactory()
        .uriString(uriBuilder.build().toString())
        .replacePath(managerEnrollPath).build().toString()
    ).contentType(APPLICATION_JSON_UTF8).body(Mono.just(
        new FabricAccount(username, password, affiliation)), FabricAccount.class
    ).exchange().filter(clientResponse -> clientResponse.statusCode().is2xxSuccessful())
        .flatMap(clientResponse ->
            clientResponse.body(BodyExtractors.toMono(ResolvableType.forClass(String.class)))
        ).block(Duration.ofSeconds(10));
    if (json == null || json.isEmpty())
      throw new IllegalStateException("system failed to init: cannot get token from fabric manager!");


    Optional<String> tokenOptional = extract(json, "/data/token");
    if (tokenOptional.isPresent()) {
      String token = "Bearer " + tokenOptional.get();
      LOG.info("access token for fabric manager: {}", token);
      return token;
    } else throw new IllegalStateException("system failed to init: cannot get token from fabric manager!");
  }

  @Bean
  Cache<String, Subscription> cache() {
    Cache<String, Subscription> cache = CacheBuilder.newBuilder()
        .initialCapacity(8).concurrencyLevel(3).build();
    LOG.info("cache init with capacity 8 and concurrency level 3");
    return cache;
  }

  public static void main(String[] args) {
    Security.addProvider(new BouncyCastleProvider());
    SpringApplication.run(App.class, args);
  }

}
