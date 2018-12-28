package response.soft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.entities.ProductSales;
import response.soft.entities.SalesBalance;
import response.soft.model.ProductSalesModel;
import response.soft.model.SalesBalanceModel;

@Service
public class SalesBalanceService extends BaseService<SalesBalance> {
    private static final Logger log = LoggerFactory.getLogger(StoreInProductService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(SalesBalance.class);
        Core.runTimeModelType.set(SalesBalanceModel.class);
    }
}
