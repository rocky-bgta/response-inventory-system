package response.soft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.entities.CustomerPaymentHistory;
import response.soft.model.CustomerPaymentHistoryModel;

@Service
public class CustomerPaymentHistoryService extends BaseService<CustomerPaymentHistory> {

    private static final Logger log = LoggerFactory.getLogger(CustomerPaymentHistoryService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(CustomerPaymentHistory.class);
        Core.runTimeModelType.set(CustomerPaymentHistoryModel.class);
    }

   /* public ResponseMessage getPreviousDueByCustomerId(String customerId) {
        ResponseMessage responseMessage;
        List<CustomerPreviousDueViewModel> list;
        CustomerPreviousDueViewModel customerPreviousDueViewModel=null;
        StringBuilder queryBuilderString = new StringBuilder();
        try {

            *//*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*//*


          *//*  SELECT
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
            GROUP BY c.name,cp.customerId*//*


            queryBuilderString.append("SELECT c.name AS customerName, ")
                    .append("cp.customerId, ")
                    .append("sum(cp.dueAmount) AS previousDue ")
                    .append("FROM CustomerPayment cp ")
                    .append("INNER JOIN Customer c ON cp.customerId = c.id ")
                    .append("WHERE cp.paidStatus = " +InventoryEnum.PaymentStatus.PARTIAL.get() +" ")
                    .append("OR cp.paidStatus = " +InventoryEnum.PaymentStatus.DUE.get() +" ")
                    .append("AND c.id = '" +customerId +"' ")
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
*/
}
