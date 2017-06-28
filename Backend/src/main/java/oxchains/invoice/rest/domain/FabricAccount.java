package oxchains.invoice.rest.domain;

/**
 * @author aiet
 */
public class FabricAccount {

  private String username;
  private String password;
  private String affiliation;

  public FabricAccount() {}

  public FabricAccount(String username, String password, String affiliation) {
    this.username = username;
    this.password = password;
    this.affiliation = affiliation;
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

  public String getAffiliation() {
    return affiliation;
  }

  public void setAffiliation(String affiliation) {
    this.affiliation = affiliation;
  }
}
