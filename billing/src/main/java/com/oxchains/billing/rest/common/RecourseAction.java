package com.oxchains.billing.rest.common;

/**
 * @author aiet
 */
public class RecourseAction extends PresentAction {

  private String borrower;

  public String getBorrower() {
    return borrower;
  }

  public void setBorrower(String borrower) {
    this.borrower = borrower;
  }
}
