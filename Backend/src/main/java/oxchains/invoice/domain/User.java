package oxchains.invoice.domain;

import javax.persistence.*;

/**
 * @author aiet
 */
@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
public class User implements IUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mobile;
    private String password;

    private String name;
    private String avatar;

    public User() {
    }

    public User(String phone, String password) {
        this.mobile = phone;
        this.password = password;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return String.format("%s(%s)", name, mobile);
    }

}
