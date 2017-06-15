package com.oxchains.billing.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.datetime.DateFormatter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * @author aiet
 */
public class Bill implements Argument {

  private String id;
  private String price;

  /**
   * 开票人/出票人
   */
  private String drawer;

  /**
   * 受票人/付款人/承兑人
   */
  private String drawee;

  /**
   * 收款人
   */
  private String payee;

  @JsonAlias("draw_date")
  @JsonFormat(pattern = "yyy-MM-dd hh:mm:ss")
  private Date date;

  @JsonFormat(pattern = "yyy-MM-dd hh:mm:ss")
  private Date due;

  private String transferable;
  private String status;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public String getDrawer() {
    return drawer;
  }

  public void setDrawer(String drawer) {
    this.drawer = drawer;
  }

  public String getDrawee() {
    return drawee;
  }

  public void setDrawee(String drawee) {
    this.drawee = drawee;
  }

  public String getPayee() {
    return payee;
  }

  public void setPayee(String payee) {
    this.payee = payee;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Date getDue() {
    return due;
  }

  public void setDue(Date due) {
    this.due = due;
  }

  public String getTransferable() {
    return transferable;
  }

  public void setTransferable(String transferable) {
    this.transferable = transferable;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String toArgs() {
    return String.format("%s,%s,%s,%s,%s,%s",
        getDrawer(), ofPattern("yyyy-MM-dd hh:mm:ss")
            .format(LocalDateTime.from(getDue().toInstant().atOffset(ZoneOffset.ofHours(0)))),
        getPrice(), getDrawee(), getPayee(), getTransferable()
    );
  }

}
