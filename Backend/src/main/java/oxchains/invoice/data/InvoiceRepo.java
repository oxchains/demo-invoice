package oxchains.invoice.data;

import org.springframework.data.repository.CrudRepository;
import oxchains.invoice.domain.Company;
import oxchains.invoice.domain.Invoice;

import java.util.Collection;
import java.util.Optional;

/**
 * @author aiet
 */
public interface InvoiceRepo extends CrudRepository<Invoice, Long> {

    Optional<Invoice> findInvoiceByOwnerAndSerial(String owner, String serial);

    Iterable<Invoice> findDistinctBySerialIn(Collection<String> serials);

    Optional<Invoice> findInvoiceByOriginAndSerial(Company origin, String serial);

    Optional<Invoice> findInvoiceByTargetAndSerial(Company target, String serial);

}
