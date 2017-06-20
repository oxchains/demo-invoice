package com.oxchains.billing.notification;

import com.google.common.cache.Cache;
import com.google.common.io.BaseEncoding;
import nl.martijndwars.webpush.ClosableCallback;
import nl.martijndwars.webpush.Encrypted;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.Utils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicHeader;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import static nl.martijndwars.webpush.PushService.encrypt;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * @author aiet
 */
@Component
public class PushHandler {

  private Logger LOG = LoggerFactory.getLogger(getClass());

  private Cache<String, Subscription> cache;

  @Autowired
  public PushHandler(Cache<String, Subscription> cache) {
    this.cache = cache;
  }

  public Mono<ServerResponse> create(ServerRequest request) {
    final String uid = request.pathVariable("uid");
    return request.bodyToMono(Subscription.class).doOnNext(subscription -> {
      cache.put(uid, subscription);
    }).flatMap(subscription -> {
      LOG.info("cached subscription of {}: {}", uid, subscription);
      try {
      /* send a test push notification */
        Notification notification = new Notification(
            subscription.getEndpoint(),
            subscription.getPublicKey(),
            subscription.getAuthAsBytes(),
            "test".getBytes()
        );
        sendAsync(notification);
      } catch (Exception e) {
        LOG.error("failed to send test notification to {}", uid, e);
      }
      return ok().build();
    }).switchIfEmpty(ok().build());
  }

  @Value("${webapp.push.key.public}")
  private String publicKey;

  @Value("${webapp.push.key.private}")
  private String privateKey;

  @Value("${webapp.push.proxy.host}")
  private String proxyHost;

  @Value("${webapp.push.proxy.port}")
  private int proxyPort;


  private void sendAsync(Notification notification) throws GeneralSecurityException, IOException, JoseException {
    BaseEncoding base64url = BaseEncoding.base64Url();

    Encrypted encrypted = encrypt(
        notification.getPayload(),
        notification.getUserPublicKey(),
        notification.getUserAuth(),
        notification.getPadSize()
    );

    byte[] dh = Utils.savePublicKey((ECPublicKey) encrypted.getPublicKey());
    byte[] salt = encrypted.getSalt();

    HttpPost httpPost = new HttpPost(notification.getEndpoint());
    httpPost.addHeader("TTL", String.valueOf(notification.getTTL()));

    Map<String, String> headers = new HashMap<>();

    if (notification.hasPayload()) {
      headers.put("Content-Type", "application/octet-stream");
      headers.put("Content-Encoding", "aesgcm");
      headers.put("Encryption", "keyid=p256dh;salt=" + base64url.omitPadding().encode(salt));
      headers.put("Crypto-Key", "keyid=p256dh;dh=" + base64url.encode(dh));

      httpPost.setEntity(new ByteArrayEntity(encrypted.getCiphertext()));
    }

    JwtClaims claims = new JwtClaims();
    claims.setAudience(notification.getOrigin());
    claims.setExpirationTimeMinutesInTheFuture(12 * 60);
    claims.setSubject("http://localhost");

    JsonWebSignature jws = new JsonWebSignature();
    jws.setHeader("typ", "JWT");
    jws.setHeader("alg", "ES256");
    jws.setPayload(claims.toJson());
    jws.setKey(Utils.loadPrivateKey(privateKey));
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256);

    headers.put("Authorization", "WebPush " + jws.getCompactSerialization());

    byte[] pk = Utils.savePublicKey((ECPublicKey) Utils.loadPublicKey(publicKey));

    if (headers.containsKey("Crypto-Key")) {
      headers.put("Crypto-Key", headers.get("Crypto-Key") + ";p256ecdsa=" + base64url.omitPadding().encode(pk));
    } else {
      headers.put("Crypto-Key", "p256ecdsa=" + base64url.encode(pk));
    }

    for (Map.Entry<String, String> entry : headers.entrySet()) {
      httpPost.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
    }

    final CloseableHttpAsyncClient closeableHttpAsyncClient = HttpAsyncClients.createDefault();
    closeableHttpAsyncClient.start();
    HttpHost proxy = new HttpHost(proxyHost, proxyPort);
    RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
    httpPost.setConfig(config);

    closeableHttpAsyncClient.execute(httpPost, new ClosableCallback(closeableHttpAsyncClient) {
      @Override
      public void completed(HttpResponse httpResponse) {
        super.completed(httpResponse);
        LOG.info("web push request done!");
      }

      @Override
      public void failed(Exception e) {
        super.failed(e);
        LOG.error("web push request failed: {}", e);
      }

      @Override
      public void cancelled() {
        super.cancelled();
        LOG.warn("web push request cancelled!");
      }

    });
  }


}
