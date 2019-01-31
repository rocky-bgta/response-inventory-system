package response.soft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import response.soft.appenum.InventoryEnum;
import response.soft.appenum.SqlEnum;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.core.ResponseMessage;
import response.soft.entities.CustomerDuePaymentHistory;
import response.soft.model.CustomerDuePaymentHistoryModel;
import response.soft.model.view.CustomerDuePaymentHistoryViewModel;
import response.soft.model.view.CustomerPreviousDueViewModel;

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
        List<CustomerDuePaymentHistoryViewModel> list;
        StringBuilder queryBuilderString = new StringBuilder();
        try {

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

    public ResponseMessage getPreviousDueByCustomerId(String customerId) {
        ResponseMessage responseMessage;
        List<CustomerPreviousDueViewModel> list;
        CustomerPreviousDueViewModel customerPreviousDueViewModel=null;
        StringBuilder queryBuilderString = new StringBuilder();
        try {

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/


          /*  SELECT
            c.name AS customerName,
                    cp.customerId,
                    sum(cp.dueAmount) AS previousDue
            FROM
            CustomerPayment cp
            INNER JOIN Customer c ON cp.customerId = c.id
            WHERE
            cp.paidStatus = 2
            OR cp.paidStatus = 3
            AND c.id = '03cee2b6-601b-4347-bb81-69503f25b31f'
            GROUP BY c.name,cp.customerId*/


            queryBuilderString.append("SELECT c.name AS customerName, ")
                    .append("cp.customerId, ")
                    .append("sum(cp.dueAmount) AS previousDue ")
                    .append("FROM CustomerPayment cp ")
                    .append("INNER JOIN Customer c ON cp.customerId = c.id ")
                    .append("WHERE (cp.paidStatus = " +InventoryEnum.PaymentStatus.PARTIAL.get() +" ")
                    .append("OR cp.paidStatus = " +InventoryEnum.PaymentStatus.DUE.get() +" ")
                    .append(") AND cp.customerId = '" +customerId +"' ")
                    .append("GROUP BY c.name,cp.customerId ");


            list = this.executeHqlQuery(queryBuilderString.toString(), CustomerPreviousDueViewModel.class, SqlEnum.QueryType.Join.get());
            if(list!=null && list.size()>0){
                customerPreviousDueViewModel = list.get(0);
            }


            responseMessage = this.buildResponseMessage(customerPreviousDueViewModel);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get Customer Previous Due successfully";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get Customer Previous Due";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("getPreviousDueByCustomerId -> save got exception");
        }
        return responseMessage;
    }



}
