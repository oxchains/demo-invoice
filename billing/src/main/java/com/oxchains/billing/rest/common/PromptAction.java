package com.oxchains.billing.rest.common;

/**
 * @author aiet
 */
public class PromptAction {

  protected String id;
  protected String manipulator;

  /**
   * 1-承兑, 0-撤销
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
}
