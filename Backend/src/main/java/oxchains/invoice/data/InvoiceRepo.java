package oxchains.invoice.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import oxchains.invoice.domain.Invoice;

import java.util.Iterator;
import java.util.Optional;

/**
 * @author aiet
 */
@Repository
public interface InvoiceRepo extends CrudRepository<Invoice, Long>{

    Optional<Invoice> findInvoiceByOwnerAndSerial(String owner, String serial);

}
