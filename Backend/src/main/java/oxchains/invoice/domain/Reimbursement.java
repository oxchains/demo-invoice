package oxchains.invoice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

/**
 * @author aiet
 */
@Entity
@Table(name = "reimbursement")
public class Reimbursement {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String serial;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company")
    private Company company;

    private String description;

    @JsonFormat(pattern = "yyy-MM-dd hh:mm:ss") private Date createTime = new Date();

    private String customer;

    @ManyToMany(fetch = FetchType.EAGER) private List<Invoice> invoices;

    private String status;

    public String reimburseArgs(String invoices) {
        setSerial(defaultIfBlank(serial, randomNumeric(7)));
        return String.format("%s,%s,%s,%s,%s,%s,%s", this.serial, invoices, this.customer, this.company.getName(), currentTimeMillis() / 1000, this.description, currentTimeMillis() / 1000 + 3600);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonGetter("createtime")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }
}
