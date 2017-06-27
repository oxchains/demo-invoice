package oxchains.invoice.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * @author aiet
 */
@Entity
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String serial;
    @Transient private Company organization;
    @Transient private Company origin;
    private Date createTime;
    private String status;
    @Transient private Goods goods;
    //TODO history

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

    public Company getOrganization() {
        return organization;
    }

    public void setOrganization(Company organization) {
        this.organization = organization;
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

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }
}
