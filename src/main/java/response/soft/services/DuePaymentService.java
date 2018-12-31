package response.soft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.entities.CustomerPayment;
import response.soft.model.CustomerPaymentModel;

@Service
public class DuePaymentService extends BaseService<CustomerPayment> {
    private static final Logger log = LoggerFactory.getLogger(StoreInProductService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(CustomerPayment.class);
        Core.runTimeModelType.set(CustomerPaymentModel.class);
    }
}
