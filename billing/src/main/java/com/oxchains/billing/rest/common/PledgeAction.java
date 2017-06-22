package com.oxchains.billing.rest.common;

/**
 * @author aiet
 */
public class PledgeAction extends PresentAction {

  private String pledgee;

  public String getPledgee() {
    return pledgee;
  }

  public void setPledgee(String pledgee) {
    this.pledgee = pledgee;
  }

  private String pledger;

  public String getPledger() {
    return pledger;
  }

  public void setPledger(String pledger) {
    this.pledger = pledger;
  }

  @Override
  public String toArgs() {
    if(action!=null) return String.format("%s,%s,%s,%s", getManipulator(), getId(), getPledger(), getAction());
    else return String.format("%s,%s,%s", getManipulator(), getId(), getPledgee());
  }

}
