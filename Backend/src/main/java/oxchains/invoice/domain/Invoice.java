package oxchains.invoice.domain;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author aiet
 */
@JsonInclude(NON_NULL)
@Entity
@Table(name = "invoice", indexes = {
  @Index(unique = true, name = "inv_serial_idx", columnList = "serial")
})
public class Invoice {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String serial;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target")
    private Company target;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "origin")
    private Company origin;
    private String owner;

    @JsonFormat(pattern = "yyy-MM-dd hh:mm:ss") private Date createTime;
    private String status;
    private int code;
    @ElementCollection(fetch = FetchType.EAGER, targetClass = Goods.class) private List<Goods> goods;

    @JsonIgnore private String history;

    public Invoice() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static Invoice fromPayload(String serial, String timestamp) {
        Invoice invoice = new Invoice();
        invoice.serial = serial;
        if (isNotBlank(timestamp)) {
            invoice.createTime = new Date(Long.valueOf(timestamp) * 1000);
        }
        return invoice;
    }

    @JsonGetter("history")
    public List<String> histories() {
        return history != null ? Arrays.asList(history.split(",")) : null;
    }

    @JsonSetter("history")
    public void setHistory(List<String> history) {
        if (history != null) {
            this.history = history
              .stream()
              .collect(joining(","));
        }
    }

    @Override
    public String toString(){
        return String.format("%s-[%s]-%s(%s)", origin, owner, target, serial);
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        if (this.history == null) {
            this.history = history;
        } else {
            this.history = this.history + "," + history;
        }
    }

    public String createArgs() {
        setSerial(defaultIfBlank(serial, randomNumeric(7)));
        setCreateTime(new Date());
        return String.format("%s,%s,%s,%s,%s", this.serial, origin.getName(), target.getName(), currentTimeMillis() / 1000, goods);
    }

    public String transferArgs(String transferTarget) {
        return String.format("%s,%s,%s,%s", this.serial, this.owner, transferTarget, currentTimeMillis() / 1000);
    }

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

    @JsonGetter("createtime")
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
