package com.oxchains.billing.rest.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.BodyInserters.empty;

/**
 * @author aiet
 */
public class ClientResponse2ServerResponse implements ServerResponse {

  private final ClientResponse clientResponse;
  private Map<String, Object> hints = new HashMap<>(0);

  private ClientResponse2ServerResponse(ClientResponse clientResponse) {
    this.clientResponse = clientResponse;
  }

  private ClientResponse2ServerResponse(ClientResponse clientResponse, Map<String, Object> hints) {
    this.clientResponse = clientResponse;
    this.hints = hints;
  }

  public static ServerResponse toServerResponse(ClientResponse clientResponse) {
    return new ClientResponse2ServerResponse(clientResponse);
  }

  @Override
  public HttpStatus statusCode() {
    return clientResponse.statusCode();
  }

  @Override
  public HttpHeaders headers() {
    return clientResponse
        .headers()
        .asHttpHeaders();
  }

  @Override
  public Mono<Void> writeTo(ServerWebExchange exchange, HandlerStrategies strategies) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(statusCode());
    HttpHeaders responseHeaders = response.getHeaders();
    HttpHeaders headers = headers();
    if (!headers.isEmpty()) {
      headers
          .entrySet()
          .stream()
          .filter(entry -> !responseHeaders.containsKey(entry.getKey()))
          .forEach(entry -> responseHeaders.put(entry.getKey(), entry.getValue()));
    }
    return empty().insert(response, new BodyInserter.Context() {
      @Override
      public Supplier<Stream<HttpMessageWriter<?>>> messageWriters() {
        return strategies.messageWriters();
      }

      @Override
      public Optional<ServerHttpRequest> serverRequest() {
        return Optional.of(exchange.getRequest());
      }

      @Override
      public Map<String, Object> hints() {
        return hints;
      }
    });
  }
}
