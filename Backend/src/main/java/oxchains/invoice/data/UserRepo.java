package oxchains.invoice.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import oxchains.invoice.domain.User;

import java.util.Optional;

/**
 * @author aiet
 */
@Repository
public interface UserRepo extends CrudRepository<User, String> {

    Optional<User> findByNameAndPassword(String username, String password);

    Optional<User> findByName(String username);

}
