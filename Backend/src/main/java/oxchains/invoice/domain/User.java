package oxchains.invoice.domain;

/**
 * @author aiet
 */
public class User {

    private String phone;
    private String password;

    public User(){}

    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public String getMobile() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
