package oxchains.invoice.rest.domain;

/**
 * @author aiet
 */
public class NameAndPass {

    private String username;
    private String password;
    private boolean biz;

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

    public boolean isBiz() {
        return biz;
    }

    public void setBiz(boolean biz) {
        this.biz = biz;
    }
}
