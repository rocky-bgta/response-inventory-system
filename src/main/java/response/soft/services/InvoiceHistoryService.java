package response.soft.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import response.soft.appenum.SqlEnum;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.entities.InvoiceHistory;
import response.soft.entities.view.InvoiceHistoryView;
import response.soft.model.InvoiceHistoryModel;

import java.util.List;

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

    public ResponseMessage getInvoiceDetailsList(RequestMessage requestMessage, String customerId) {
        ResponseMessage responseMessage;
        List<InvoiceHistoryView> list;
        String searchKey;
        StringBuilder queryBuilderString =new StringBuilder();
        try {
            Core.processRequestMessage(requestMessage);
            searchKey = Core.dataTableSearchKey.get();

            if(searchKey!=null && !StringUtils.equals(searchKey,"string")) {
                searchKey = searchKey.trim().toLowerCase();
            }

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/
            //============ full text search ===========================================

            if (searchKey != null && !StringUtils.isEmpty(searchKey)) {

                queryBuilderString.append("SELECT v FROM InvoiceHistoryView v ")
                        .append("WHERE ")
                        .append("( ")
                        .append("lower(v.customerName) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.invoiceNo) LIKE '%" + searchKey + "%' ")
                        .append("OR CAST(v.invoiceAmount AS string) LIKE '%" + searchKey + "%' ")
                        .append("OR CAST(v.discountAmount AS string) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.invoiceStatus) LIKE '%" + searchKey + "%' ")
                        .append(") ");
                if(!StringUtils.isEmpty(customerId)){
                    queryBuilderString.append("AND v.customerId='"+customerId+"'");
                }

                list = this.executeHqlQuery(queryBuilderString.toString(),InvoiceHistoryView.class,SqlEnum.QueryType.View.get());
                //============ full text search ===========================================
            }else {
                queryBuilderString.setLength(0);
                queryBuilderString.append("SELECT v FROM InvoiceHistoryView v ");
                if(!StringUtils.isEmpty(customerId)){
                    queryBuilderString.append("WHERE v.customerId='"+customerId+"'");
                }
                list = this.executeHqlQuery( queryBuilderString.toString(),InvoiceHistoryView.class,SqlEnum.QueryType.View.get());
            }

            responseMessage = this.buildResponseMessage(list);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get all Invoice Details successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get Invoice Details";
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            log.error("getInvoiceDetailsList -> save got exception");
        }
        return responseMessage;
    }

}
