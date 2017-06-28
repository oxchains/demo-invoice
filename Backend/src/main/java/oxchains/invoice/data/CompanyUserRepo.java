package oxchains.invoice.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import oxchains.invoice.domain.CompanyUser;

import java.util.Optional;

/**
 * @author aiet
 */
public interface CompanyUserRepo extends CrudRepository<CompanyUser, Long>{

    Optional<CompanyUser> findByName(String name);

    Optional<CompanyUser> findByNameAndPassword(String name, String password);


}
