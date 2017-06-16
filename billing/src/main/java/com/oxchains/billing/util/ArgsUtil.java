package com.oxchains.billing.util;

import com.oxchains.billing.domain.Argument;

/**
 * @author aiet
 */
public class ArgsUtil {

  public static String args(String action, Argument argument) {
    return args(action, argument.toArgs());
  }

  public static String args(String action, String arguments) {
    return String.format("%s,%s", action, arguments);
  }

}
