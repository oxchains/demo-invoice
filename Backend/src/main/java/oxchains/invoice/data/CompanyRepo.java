package oxchains.invoice.data;

import org.springframework.data.repository.CrudRepository;
import oxchains.invoice.domain.Company;

import java.util.Optional;

/**
 * @author aiet
 */
public interface CompanyRepo extends CrudRepository<Company, Long> {

    Optional<Company> findByName(String name);

}
