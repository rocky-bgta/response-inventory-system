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
import response.soft.core.datatable.model.DataTableRequest;
import response.soft.entities.CustomerPayment;
import response.soft.model.CustomerPaymentModel;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerPaymentService extends BaseService<CustomerPayment> {
    private static final Logger log = LoggerFactory.getLogger(StoreInProductService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(CustomerPayment.class);
        Core.runTimeModelType.set(CustomerPaymentModel.class);
    }

    public ResponseMessage getCustomerPaymentList(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        List<CustomerPaymentModel> list=null;
        DataTableRequest dataTableRequest;
        String searchKey=null;
        StringBuilder queryBuilderString =new StringBuilder();
        try {
            Core.processRequestMessage(requestMessage);

            dataTableRequest = requestMessage.dataTableRequest;
            if(dataTableRequest!=null && !StringUtils.equals(dataTableRequest.search.value,"string")) {
                searchKey = dataTableRequest.search.value;
                searchKey = searchKey.trim().toLowerCase();
            }

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/



            //============ full text search ===========================================

            if ((dataTableRequest != null && !StringUtils.isEmpty(searchKey))) {


             /*   queryBuilderString.append("SELECT cp.id, ")
                        .append("cp.customerId, ")
                        .append("cp.invoiceNo, ")
                        .append("cp.paidAmount, ")
                        .append("cp.dueAmount, ")
                        .append("cp.grandTotal, ")
                        .append("cp.paidStatus, ")
                        .append("cp.invoiceDate, ")
                        .append("c.name as customerName ")*/

               queryBuilderString.append("SELECT cp.id, ")
                           .append("cp.customerId, ")
                           .append("cp.invoiceNo, ")
                           .append("cp.paidAmount, ")
                           .append("cp.dueAmount, ")
                           .append("cp.grandTotal, ")
                           .append("cp.paidStatus, ")
                           .append("cp.invoiceDate, ")
                           .append("c.name as customerName")
                        .append("WHERE ")
                        .append("( ")
                        .append("OR lower(cp.invoiceNo) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(cp.paidAmount) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(cp.dueAmount) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(cp.grandTotal) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(cp.paidStatus) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(cp.date) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(c.name) LIKE '%" + searchKey + "%' ")
                        .append(") ")
                        .append("AND v.status="+SqlEnum.Status.Active.get());


                //list = this.executeHqlQuery(queryBuilderString.toString(),AvailableStockView.class,SqlEnum.QueryType.View.get());
                //============ full text search ===========================================
            }else {
                queryBuilderString.setLength(0);
                queryBuilderString.append("SELECT cp.id, ")
                        .append("cp.customerId, ")
                        .append("cp.invoiceNo, ")
                        .append("cp.paidAmount, ")
                        .append("cp.dueAmount, ")
                        .append("cp.grandTotal, ")
                        .append("cp.paidStatus, ")
                        .append("cp.invoiceDate, ")
                        .append("cp.paymentDate, ")
                        .append("c.name as customerName ")
                .append("FROM CustomerPayment cp ")
                .append("INNER JOIN Customer c ON cp.customerId = c.id");

                list = this.executeHqlQuery(queryBuilderString.toString(),CustomerPaymentModel.class,SqlEnum.QueryType.Join.get());
            }

            responseMessage = this.buildResponseMessage();

            if (list != null && list.size()>0) {
                responseMessage.data = list;
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get all Customer payment list successfully";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get Customer payment list";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("getCustomerPaymentList -> save got exception");
        }
        return responseMessage;
    }
}
