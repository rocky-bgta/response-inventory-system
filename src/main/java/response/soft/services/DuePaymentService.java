package response.soft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.entities.DuePayment;
import response.soft.entities.SalesBalance;
import response.soft.model.DuePaymentModel;
import response.soft.model.SalesBalanceModel;

@Service
public class DuePaymentService extends BaseService<DuePayment> {
    private static final Logger log = LoggerFactory.getLogger(StoreInProductService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(DuePayment.class);
        Core.runTimeModelType.set(DuePaymentModel.class);
    }
}
