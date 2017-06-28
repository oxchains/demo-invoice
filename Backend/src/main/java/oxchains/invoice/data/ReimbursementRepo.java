package oxchains.invoice.data;

import org.springframework.data.repository.CrudRepository;
import oxchains.invoice.domain.Company;
import oxchains.invoice.domain.CompanyUser;
import oxchains.invoice.domain.Reimbursement;

import java.util.Optional;

/**
 * @author aiet
 */
public interface ReimbursementRepo extends CrudRepository<Reimbursement, Long> {

    Optional<Reimbursement> findBySerialAndCompany(String serial, Company company);

    Optional<Reimbursement> findBySerialAndCustomer(String serial, String customer);

    Iterable<Reimbursement> findAllByCustomer(String customer);

    Iterable<Reimbursement> findAllByCompany(Company company);

}
