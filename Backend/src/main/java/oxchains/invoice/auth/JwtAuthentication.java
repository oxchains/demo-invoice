package oxchains.invoice.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import oxchains.invoice.domain.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * @author aiet
 */
public class JwtAuthentication implements Authentication {

    private String token;
    private User user;
    private Map<String, Object> details;

    JwtAuthentication(User user, String token, Map<String, Object> details) {
        this.user = user;
        this.token = token;
        this.details = details;
    }

    public Optional<User> user() {
        return Optional.ofNullable(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return emptyList();
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    @Override
    public boolean isAuthenticated() {
        return user != null && user.getName() != null && user.getMobile() != null;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (!isAuthenticated) user = null;
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public String toString() {
        return token;
    }

}
