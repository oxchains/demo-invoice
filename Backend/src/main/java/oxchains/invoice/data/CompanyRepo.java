package oxchains.invoice.data;

import org.springframework.data.repository.CrudRepository;
import oxchains.invoice.domain.Company;

/**
 * @author aiet
 */
public interface CompanyRepo extends CrudRepository<Company, Long> {

}
