package com.oxchains.billing.notification;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * @author aiet
 */
public class Subscription {

  private String auth;
  private String key;
  private String endpoint;

  public String getAuth() {
    return auth;
  }

  public void setAuth(String auth) {
    this.auth = auth;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }


  /**
   * Returns the base64 encoded auth string as a byte[]
   */
  public byte[] getAuthAsBytes() {
    return asBytes(getAuth());
  }

  /**
   * Returns the base64 encoded public key string as a byte[]
   */
  private byte[] asBytes(String key) {
    return Base64.getDecoder().decode(key);
  }

  /**
   * Returns the base64 encoded public key as a PublicKey object
   */
  public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
    KeyFactory kf = KeyFactory.getInstance("ECDH", BouncyCastleProvider.PROVIDER_NAME);
    ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
    ECPoint point = ecSpec.getCurve().decodePoint(asBytes(getKey()));
    ECPublicKeySpec pubSpec = new ECPublicKeySpec(point, ecSpec);
    return kf.generatePublic(pubSpec);
  }

  @Override
  public String toString(){
    return String.format("auth: %s, key: %s, endpoint: %s", getAuth(), getKey(), getEndpoint());
  }


}
