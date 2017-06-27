package oxchains.invoice.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import oxchains.invoice.domain.CompanyUser;

import java.util.Optional;

/**
 * @author aiet
 */
@Repository
public interface CompanyUserRepo extends CrudRepository<CompanyUser, Long>{

    Optional<CompanyUser> findByName(String name);


}
