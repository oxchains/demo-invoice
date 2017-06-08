package com.oxchains.billing.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author aiet
 */
public class Bill {

    private String id;
    private String price;

    /**
     * 开票人
     */
    private String drawer;

    /**
     * 受票人/付款人
     */
    private String drawee;

    /**
     * 收款人
     */
    private String payee;

    @JsonAlias("draw_date")
    @JsonFormat(pattern = "yyy-MM-dd hh:mm:ss")
    private Date date;
    private String due;
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

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
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
}
