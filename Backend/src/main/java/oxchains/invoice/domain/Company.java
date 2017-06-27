package oxchains.invoice.domain;

import com.fasterxml.jackson.annotation.JsonGetter;

import javax.persistence.*;

/**
 * @author aiet
 */
@Entity
@Table(name = "company")
public class Company {

    public Company() {
    }

    public Company(String name, String taxIdentifier, String address, String bankName, String bankAccount) {
        this.name = name;
        this.taxIdentifier = taxIdentifier;
        this.address = address;
        this.bankName = bankName;
        this.bankAccount = bankAccount;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Column(name = "tax_identifier") private String taxIdentifier;

    private String address;

    @Column(name = "bank_name") private String bankName;
    @Column(name = "bank_account") private String bankAccount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonGetter("taxpayer")
    public String getTaxIdentifier() {
        return taxIdentifier;
    }

    public void setTaxIdentifier(String taxIdentifier) {
        this.taxIdentifier = taxIdentifier;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @JsonGetter("bank")
    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @JsonGetter("account")
    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

}
