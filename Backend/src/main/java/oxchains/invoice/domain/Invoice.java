package oxchains.invoice.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author aiet
 */
@Entity
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String serial;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target")
    private Company target;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "origin")
    private Company origin;
    private String owner;
    private Date createTime;
    private String status;
    @ElementCollection(fetch = FetchType.EAGER, targetClass = Goods.class) private List<Goods> goods;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public Company getTarget() {
        return target;
    }

    public void setTarget(Company target) {
        this.target = target;
    }

    public Company getOrigin() {
        return origin;
    }

    public void setOrigin(Company origin) {
        this.origin = origin;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Goods> getGoods() {
        return goods;
    }

    public void setGoods(List<Goods> goods) {
        this.goods = goods;
    }
}
