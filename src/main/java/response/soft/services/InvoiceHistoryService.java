package response.soft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.entities.InvoiceHistory;
import response.soft.model.InvoiceHistoryModel;

@Service
public class InvoiceHistoryService extends BaseService<InvoiceHistory> {
    private static final Logger log = LoggerFactory.getLogger(StoreInProductService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(InvoiceHistory.class);
        Core.runTimeModelType.set(InvoiceHistoryModel.class);
    }
}
