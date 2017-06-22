package oxchains.invoice.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author aiet
 */
public class CompanyUser implements IUser, ICompany{

    private Company company = new Company();
    private User user = new User();

    public void setUser(User user) {
        this.user = user;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @JsonIgnore
    public Company getCompany() {
        return company;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setPhone(String phone) {
        user.setPhone(phone);
    }

    public void setPassword(String password) {
        user.setPassword(password);
    }

    public void setName(String name) {
        company.setName(name);
    }

    @JsonSetter("taxpayer")
    public void setTaxIdentifier(String taxIdentifier) {
        company.setTaxIdentifier(taxIdentifier);
    }

    public void setAddress(String address) {
        company.setAddress(address);
    }

    @JsonSetter("bank")
    public void setBankName(String bankName) {
        company.setBankName(bankName);
    }

    @JsonSetter("account")
    public void setBankAccount(String bankAccount) {
        company.setBankAccount(bankAccount);
    }

    @Override
    public String getMobile() {
        return user.getMobile();
    }

    @Override
    public String getName() {
        return company.getName();
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
