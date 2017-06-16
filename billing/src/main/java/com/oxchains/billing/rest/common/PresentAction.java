package com.oxchains.billing.rest.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oxchains.billing.domain.Argument;

/**
 * @author aiet
 */
public class PresentAction implements Argument{

  @JsonIgnore
  protected Class clazz = PresentAction.class;
  protected String id;
  protected String manipulator;

  public Class getClazz() {
    return clazz;
  }

  public void setClazz(Class clazz) {
    this.clazz = clazz;
  }

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
    if(action!=null) return String.format("%s,%s,%s", getManipulator(), getId(), getAction());
    else return String.format("%s,%s", getManipulator(), getId());
  }

}
