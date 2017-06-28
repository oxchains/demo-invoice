package oxchains.invoice.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import oxchains.invoice.domain.UserToken;

/**
 * @author aiet
 */
public interface UserTokenRepo extends CrudRepository<UserToken, String> {
}
