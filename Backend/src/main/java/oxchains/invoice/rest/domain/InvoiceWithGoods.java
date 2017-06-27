package oxchains.invoice.rest.domain;

import com.fasterxml.jackson.annotation.JsonSetter;
import oxchains.invoice.domain.Goods;

/**
 * @author aiet
 */
public class InvoiceWithGoods {

    private Goods goods;

    public Goods getGoods() {
        return goods;
    }

    private synchronized Goods nonNullGoods(){
        if(goods==null) goods = new Goods();
        return goods;
    }

    @JsonSetter("name")
    public void setName(String name) {
        nonNullGoods().setName(name);
    }

    @JsonSetter("description")
    public void setDescription(String description) {
        nonNullGoods().setDescription(description);
    }

    @JsonSetter("quantity")
    public void setQuantity(int quantity) {
        nonNullGoods().setQuantity(quantity);
    }

    @JsonSetter("price")
    public void setPrice(int price) {
        nonNullGoods().setPrice(price);
    }

    private String serial;
    private String title;
    private String issuer;

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
