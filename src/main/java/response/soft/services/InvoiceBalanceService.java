package response.soft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.entities.CustomerPayment;
import response.soft.entities.InvoiceBalance;
import response.soft.model.CustomerPaymentModel;
import response.soft.model.InvoiceBalanceModel;

@Service
public class InvoiceBalanceService extends BaseService<InvoiceBalance> {
    private static final Logger log = LoggerFactory.getLogger(StoreInProductService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(InvoiceBalance.class);
        Core.runTimeModelType.set(InvoiceBalanceModel.class);
    }
}
