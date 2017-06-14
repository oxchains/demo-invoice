package com.oxchains.billing.rest.common;

/**
 * @author aiet
 */
public class GuaranteeAction extends PresentAction{

  private String guarantor;

  public String getGuarantor() {
    return guarantor;
  }

  public void setGuarantor(String guarantor) {
    this.guarantor = guarantor;
  }

  @Override
  public String toArgs() {
    if(action!=null) return String.format("%s,%s,%s,%s", getManipulator(), getId(), getGuarantor(), getAction());
    else return String.format("%s,%s,%s", getManipulator(), getId(), getGuarantor());
  }

}
