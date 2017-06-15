package com.oxchains.billing.rest.common;

/**
 * @author aiet
 */
public class EndorseAction extends PresentAction {

  private String endorsee;

  public String getEndorsee() {
    return endorsee;
  }

  public void setEndorsee(String endorsee) {
    this.endorsee = endorsee;
  }

  private String endorsor;

  public String getEndorsor() {
    return endorsor;
  }

  public void setEndorsor(String endorsor) {
    this.endorsor = endorsor;
  }

  @Override
  public String toArgs() {
    if(action!=null) return String.format("%s,%s,%s,%s", getManipulator(), getId(), getEndorsor(), getAction());
    else return String.format("%s,%s,%s", getManipulator(), getId(), getEndorsee());
  }


}
