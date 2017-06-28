package oxchains.invoice.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author aiet
 */
@Entity
@Table(name = "reimbursement")
public class Reimbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String serial;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company")
    private Company company;

    private String department;
    private Date createTime;

    @Transient private User by;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Invoice> invoices;

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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public User getBy() {
        return by;
    }

    public void setBy(User by) {
        this.by = by;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }
}
