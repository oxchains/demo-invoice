package com.oxchains.billing;

import com.oxchains.billing.domain.FabricAccount;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
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
@EnableScheduling
public class App {

  private Logger LOG = LoggerFactory.getLogger(getClass());

  public static final TokenHolder TOKEN_HOLDER = new TokenHolder();

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
    uriBuilder
        .path(managerTransactionPath)
        .queryParam("chain", chain)
        .queryParam("chaincode", chaincode)
        .queryParam("version", chaincodeVer)
        .queryParam("args", "");
    TOKEN_HOLDER.uri = uriBuilder.build().toString();
    refreshToken();
    return uriBuilder;
  }

  public static class TokenHolder {
    private String token;
    private String uri;

    public String getToken() {
      return token;
    }

    private void setToken(String token) {
      this.token = token;
    }
  }

  @Scheduled(fixedRate = 1000 * 3600 * 72)
  void refreshToken() {
    String json = (String) WebClient.create().post().uri(new DefaultUriBuilderFactory()
        .uriString(TOKEN_HOLDER.uri)
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
      TOKEN_HOLDER.setToken(token);
    } else throw new IllegalStateException("system failed to init: cannot get token from fabric manager!");
  }

  public static void main(String[] args) {
    Security.addProvider(new BouncyCastleProvider());
    SpringApplication.run(App.class, args);
  }

}
