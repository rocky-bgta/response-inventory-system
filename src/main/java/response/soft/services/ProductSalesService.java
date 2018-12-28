package response.soft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.entities.ProductSales;
import response.soft.entities.StoreOutProduct;
import response.soft.model.ProductSalesModel;
import response.soft.model.StoreOutProductModel;

@Service
public class ProductSalesService extends BaseService<ProductSales> {
    private static final Logger log = LoggerFactory.getLogger(StoreInProductService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(ProductSales.class);
        Core.runTimeModelType.set(ProductSalesModel.class);
    }
}
