package com.oxchains.billing.rest.common;

/**
 * @author aiet
 */
public class RecourseAction extends PresentAction {

  private String debtor;

  public String getDebtor() {
    return debtor;
  }

  public void setDebtor(String debtor) {
    this.debtor = debtor;
  }

  @Override
  public String toArgs() {
    if(action!=null) return String.format("%s,%s,%s,%s", getManipulator(), getId(), getDebtor(), getAction());
    else return String.format("%s,%s,%s", getManipulator(), getId(), getDebtor());
  }

}
