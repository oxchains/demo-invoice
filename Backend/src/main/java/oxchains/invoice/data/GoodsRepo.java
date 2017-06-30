package oxchains.invoice.data;

import org.springframework.data.repository.CrudRepository;
import oxchains.invoice.domain.Goods;

/**
 * @author aiet
 */
public interface GoodsRepo extends CrudRepository<Goods, Long> {
}
