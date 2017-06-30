package oxchains.invoice.rest.domain;

import oxchains.invoice.domain.Goods;

import java.util.List;

/**
 * @author aiet
 */
public class InvoiceReq {

    private String target;

    private String title;

    private List<Goods> goods;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<Goods> getGoods() {
        return goods;
    }

    public void setGoods(List<Goods> goods) {
        this.goods = goods;
    }
}
