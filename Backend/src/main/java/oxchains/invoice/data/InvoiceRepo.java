package oxchains.invoice.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import oxchains.invoice.domain.Invoice;

import java.util.Iterator;

/**
 * @author aiet
 */
@Repository
public interface InvoiceRepo extends CrudRepository<Invoice, Long>{

    Iterable<Invoice> findInvoicesByOwnerOrOriginOrTarget(String owner, Long origin, Long target);

}
