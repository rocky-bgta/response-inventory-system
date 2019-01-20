package response.soft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import response.soft.appenum.SqlEnum;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.core.ResponseMessage;
import response.soft.entities.CustomerDuePaymentHistory;
import response.soft.model.CustomerDuePaymentHistoryModel;
import response.soft.model.view.CustomerDuePaymentHistoryViewModel;

import java.util.List;

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


    public ResponseMessage getCustomerPaymentHistoryByInvoiceNo(String invoiceNo) {
        ResponseMessage responseMessage;
        List<CustomerDuePaymentHistoryViewModel> list = null;
        String searchKey = null;
        StringBuilder queryBuilderString = new StringBuilder();
        try {
            this.resetPaginationVariable();
            //Core.processRequestMessage(requestMessage);
            searchKey = Core.dataTableSearchKey.get();

           /* if(searchKey!=null && !StringUtils.equals(searchKey,"string")) {
                searchKey = searchKey.trim().toLowerCase();
            }*/

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            queryBuilderString.append("SELECT c.id AS customerId, ")
                    .append("c.name AS customerName, ")
                    .append("cdph.invoiceNo, ")
                    .append("cdph.paidAmount, ")
                    .append("cdph.paymentDate ")
                    .append("FROM CustomerDuePaymentHistory cdph ")
                    .append("INNER JOIN CustomerPayment cp ON cdph.invoiceNo = cp.invoiceNo ")
                    .append("INNER JOIN Customer c ON cp.customerId = c.id ")
                    .append("WHERE cdph.invoiceNo = '" + invoiceNo + "'");


            list = this.executeHqlQuery(queryBuilderString.toString(), CustomerDuePaymentHistoryViewModel.class, SqlEnum.QueryType.Join.get());


            responseMessage = this.buildResponseMessage(list);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get Customer Payment history successfully";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get Customer Payment history";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("getAllStock -> save got exception");
        }
        return responseMessage;
    }

}
