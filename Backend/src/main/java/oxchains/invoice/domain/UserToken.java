package oxchains.invoice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author aiet
 */
@Entity
@Table(name = "user_token")
public class UserToken {

    @Id private Long id;
    @Column(length = 512) private String token;
    @JsonFormat(pattern = "yyy-MM-dd hh:mm:ss") private Date createtime = new Date();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    private User user;

    public UserToken() {
    }

    public UserToken(User u, String token) {
        this.user = u;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

}
