package response.soft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.entities.Invoice_Balance;
import response.soft.model.SalesBalanceModel;

@Service
public class SalesBalanceService extends BaseService<Invoice_Balance> {
    private static final Logger log = LoggerFactory.getLogger(StoreInProductService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(Invoice_Balance.class);
        Core.runTimeModelType.set(SalesBalanceModel.class);
    }
}
