package oxchains.invoice.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.persistence.*;

/**
 * @author aiet
 */
@Entity
@Table(name = "company_user")
public class CompanyUser implements IUser {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company")
    private Company company;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String password;
    private String avatar;
    private String mobile;

    @Override
    public String toString(){
        return String.format("%s(%s)", name, mobile);
    }


    public void withUser(User user) {
        this.name = user.getName();
        this.password = user.getPassword();
        this.avatar = user.getAvatar();
        this.mobile = user.getMobile();
    }

    public User toUser() {
        User user = new User(this.mobile, this.password);
        user.setId(this.id);
        user.setName(this.name);
        user.setAvatar(this.avatar);
        user.setBiz(true);
        return user;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @JsonIgnore
    public Company getCompany() {
        return company;
    }

    private synchronized Company nonNullCompany() {
        if (company == null) company = new Company();
        return company;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        nonNullCompany().setName(name);
        this.name = name;
    }

    public String getName() {
        return company.getName();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @JsonSetter("taxpayer")
    public void setTaxIdentifier(String taxIdentifier) {
        nonNullCompany().setTaxIdentifier(taxIdentifier);
    }

    @JsonSetter("address")
    public void setAddress(String address) {
        nonNullCompany().setAddress(address);
    }

    @JsonSetter("bank")
    public void setBankName(String bankName) {
        nonNullCompany().setBankName(bankName);
    }

    @JsonSetter("account")
    public void setBankAccount(String bankAccount) {
        nonNullCompany().setBankAccount(bankAccount);
    }

    @JsonGetter("taxpayer")
    public String getTaxIdentifier() {
        return company.getTaxIdentifier();
    }

    public String getAddress() {
        return company.getAddress();
    }

    @JsonGetter("bank")
    public String getBankName() {
        return company.getBankName();
    }

    @JsonGetter("account")
    public String getBankAccount() {
        return company.getBankAccount();
    }
}
