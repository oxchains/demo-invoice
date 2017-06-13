package com.oxchains.billing.rest.common;

import com.oxchains.billing.domain.Argument;

/**
 * @author aiet
 */
public class PresentAction implements Argument{

  protected String id;
  protected String manipulator;

  /**
   * 1-确认, 0-撤销
   */
  protected String action;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getManipulator() {
    return manipulator;
  }

  public void setManipulator(String manipulator) {
    this.manipulator = manipulator;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  @Override
  public String toArgs() {
    return String.format("%s,%s,%s", getManipulator(), getId(), getAction());
  }

}
