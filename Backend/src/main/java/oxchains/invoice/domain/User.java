package oxchains.invoice.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author aiet
 */
@Entity
public class User implements IUser {

    private String mobile;
    private String password;
    @Id private String name;
    private String avatar;

    public User() {
    }

    public User(String phone, String password) {
        this.mobile = phone;
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
