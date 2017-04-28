package oxchains.fabric.sdk.domain;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.util.Set;

/**
 * @author aiet
 */
public class FabricUser implements User {

    private String name;
    private Set<String> roles;
    private String account;
    private String affiliation;
    private Enrollment enrollment;
    private String mspId;
    private String enrollmentSecret;

    public void setEnrollmentSecret(String enrollmentSecret) {
        this.enrollmentSecret = enrollmentSecret;
    }

    public String getEnrollmentSecret() {
        return enrollmentSecret;
    }

    public void setMspId(String mspId) {
        this.mspId = mspId;
    }

    public FabricUser(String name, String affiliation) {
        this.name = name;
        this.affiliation = affiliation;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<String> getRoles() {
        return this.roles;
    }

    @Override
    public String getAccount() {
        return this.account;
    }

    @Override
    public String getAffiliation() {
        return this.affiliation;
    }

    @Override
    public Enrollment getEnrollment() {
        return this.enrollment;
    }

    @Override
    public String getMSPID() {
        return null;
    }
}
