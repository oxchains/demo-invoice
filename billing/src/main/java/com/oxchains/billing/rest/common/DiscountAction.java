package com.oxchains.billing.rest.common;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * @author aiet
 */
public class DiscountAction extends PresentAction {

  private String receiver;
  @JsonAlias("discount_type")
  private String type;
  @JsonAlias("discount_interest")
  private String interest;
  private String money;

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getInterest() {
    return interest;
  }

  public void setInterest(String interest) {
    this.interest = interest;
  }

  public String getMoney() {
    return money;
  }

  public void setMoney(String money) {
    this.money = money;
  }
}
