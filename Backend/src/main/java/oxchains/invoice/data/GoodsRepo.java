package oxchains.invoice.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import oxchains.invoice.domain.Goods;

/**
 * @author aiet
 */
@Repository
public interface GoodsRepo extends CrudRepository<Goods, Long>{
}
