package com.oxchains.billing.rest.common;

import com.oxchains.billing.domain.Bill;
import com.oxchains.billing.util.ResponseUtil;
import org.springframework.core.ResolvableType;
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

import static com.oxchains.billing.util.ResponseUtil.payloadToBillResp;
import static org.springframework.web.reactive.function.BodyExtractors.toMono;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;

/**
 * @author aiet
 */
public class ClientResponse2ServerResponse implements ServerResponse {

  private final HttpStatus statusCode;
  private final HttpHeaders httpHeaders;
  private final Mono<String> body;

  private final Mono<Bill> billSink;

  private Map<String, Object> hints = new HashMap<>(0);

  private ClientResponse2ServerResponse(ClientResponse clientResponse, boolean transformPayload) {
    this.statusCode = clientResponse.statusCode();
    this.httpHeaders = clientResponse.headers().asHttpHeaders();
    Mono<String> response = clientResponse.body(toMono(ResolvableType.forClass(String.class)));
    this.body = response.map(resp -> transformPayload ? payloadToBillResp(resp) : resp);
    this.billSink = response.map(ResponseUtil::payloadToBill);
  }

  public Mono<Bill> billSink() {
    return billSink;
  }

  private ClientResponse2ServerResponse(ClientResponse clientResponse, Map<String, Object> hints) {
    this(clientResponse, false);
    this.hints = hints;
  }

  public static ServerResponse toServerResponse(ClientResponse clientResponse) {
    return new ClientResponse2ServerResponse(clientResponse, false);
  }

  public static ServerResponse toPayloadTransformedServerResponse(ClientResponse clientResponse) {
    return new ClientResponse2ServerResponse(clientResponse, true);
  }

  @Override
  public HttpStatus statusCode() {
    return this.statusCode;
  }

  @Override
  public HttpHeaders headers() {
    return this.httpHeaders;
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
    return fromPublisher(body, String.class).insert(response, new BodyInserter.Context() {
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
