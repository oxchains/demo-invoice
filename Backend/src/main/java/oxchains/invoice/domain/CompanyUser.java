package oxchains.invoice.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.persistence.*;

/**
 * @author aiet
 */
@Entity
public class CompanyUser implements IUser, ICompany {

    @Transient private Company company;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String avatar;
    private String mobile;

    public void setUser(User user) {
        this.username = user.getName();
        this.password = user.getPassword();
        this.avatar = user.getAvatar();
        this.mobile = user.getMobile();
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @JsonIgnore
    public Company getCompany() {
        return company;
    }

    private synchronized Company nonNullCompany(){
        if(company==null) company = new Company();
        return company;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonSetter("name")
    public void setName(String name) {
        nonNullCompany().setName(name);
    }

    public String getName() {
        return company.getName();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
    @Override
    public String getTaxIdentifier() {
        return company.getTaxIdentifier();
    }

    @Override
    public String getAddress() {
        return company.getAddress();
    }

    @JsonGetter("bank")
    @Override
    public String getBankName() {
        return company.getBankName();
    }

    @JsonGetter("account")
    @Override
    public String getBankAccount() {
        return company.getBankAccount();
    }
}
