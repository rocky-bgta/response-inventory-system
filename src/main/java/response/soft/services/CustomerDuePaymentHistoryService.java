package response.soft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.entities.CustomerDuePaymentHistory;
import response.soft.model.CustomerDuePaymentHistoryModel;

@Service
public class CustomerDuePaymentHistoryService extends BaseService<CustomerDuePaymentHistory> {

    private static final Logger log = LoggerFactory.getLogger(CustomerDuePaymentHistoryService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(CustomerDuePaymentHistory.class);
        Core.runTimeModelType.set(CustomerDuePaymentHistoryModel.class);
    }

}
