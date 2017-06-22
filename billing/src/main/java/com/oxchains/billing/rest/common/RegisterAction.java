package com.oxchains.billing.rest.common;

import com.oxchains.billing.domain.Argument;

/**
 * @author aiet
 */
public class RegisterAction implements Argument{

  private String user;
  private String asset;

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getAsset() {
    return asset;
  }

  public void setAsset(String asset) {
    this.asset = asset;
  }

  @Override
  public String toArgs() {
    return String.format("%s,%s", user, asset);
  }

}
