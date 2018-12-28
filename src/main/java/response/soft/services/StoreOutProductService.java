package response.soft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.entities.StoreOutProduct;
import response.soft.model.StoreOutProductModel;

@Service
public class StoreOutProductService extends BaseService<StoreOutProduct> {
    private static final Logger log = LoggerFactory.getLogger(StoreInProductService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(StoreOutProduct.class);
        Core.runTimeModelType.set(StoreOutProductModel.class);
    }
}
