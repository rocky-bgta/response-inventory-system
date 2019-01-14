package response.soft.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import response.soft.Utils.AppUtils;
import response.soft.appenum.InventoryEnum;
import response.soft.appenum.SqlEnum;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.core.datatable.model.DataTableRequest;
import response.soft.entities.CustomerPayment;
import response.soft.model.CustomerDuePaymentHistoryModel;
import response.soft.model.CustomerPaymentModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerPaymentService extends BaseService<CustomerPayment> {
    private static final Logger log = LoggerFactory.getLogger(StoreInProductService.class);

    @Autowired
    CustomerDuePaymentHistoryService customerDuePaymentHistoryService;

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
                        .append("INNER JOIN Customer c ON cp.customerId = c.id ")
                        .append("WHERE ")
                        .append("( ")
                        .append("lower(cp.invoiceNo) LIKE '%" + searchKey + "%' ")
                        .append("OR CAST(cp.paidAmount AS string) LIKE '%" + searchKey + "%' ")
                        .append("OR CAST(cp.dueAmount AS string) LIKE '%" + searchKey + "%' ")
                        .append("OR CAST(cp.grandTotal AS string) LIKE '%" + searchKey + "%' ")
                        //.append("OR lower(cp.date) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(c.name) LIKE '%" + searchKey + "%' ")
                        .append(") ")
                        .append("AND ( cp.paidStatus = "+InventoryEnum.PaymentStatus.PARTIAL.get())
                        .append(" OR cp.paidStatus = " + InventoryEnum.PaymentStatus.DUE.get() +" )");


                list = this.executeHqlQuery(queryBuilderString.toString(),CustomerPaymentModel.class,SqlEnum.QueryType.Join.get());
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
                        .append("INNER JOIN Customer c ON cp.customerId = c.id ")
                        .append("WHERE cp.paidStatus = "+InventoryEnum.PaymentStatus.PARTIAL.get())
                        .append(" OR cp.paidStatus = " + InventoryEnum.PaymentStatus.DUE.get());

                list = this.executeHqlQuery(queryBuilderString.toString(),CustomerPaymentModel.class,SqlEnum.QueryType.Join.get());
            }



            if (list != null && list.size()>0) {
                responseMessage = this.buildResponseMessage(list);
                responseMessage.data = list;
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get all Customer payment list successfully";
                //this.commit();
            } else {
                responseMessage = this.buildResponseMessage();
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

    public ResponseMessage updateCustomerPayment(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        CustomerPaymentModel requestedCustomerPaymentModel, createdCustomerPaymentModel;
        Integer paidStatus;
        Double currentPayment;
        Double dueAmount;
        CustomerDuePaymentHistoryModel customerDuePaymentHistoryModel;

        try {

             /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            requestedCustomerPaymentModel = Core.processRequestMessage(requestMessage,CustomerPaymentModel.class);

            dueAmount = requestedCustomerPaymentModel.getDueAmount();
            currentPayment = requestedCustomerPaymentModel.getCurrentPayment();

            dueAmount = dueAmount - currentPayment;

            if(dueAmount==0)
                paidStatus = InventoryEnum.PaymentStatus.PAID.get();
            else
                paidStatus = InventoryEnum.PaymentStatus.PARTIAL.get();

            requestedCustomerPaymentModel.setDueAmount(dueAmount);
            requestedCustomerPaymentModel.setPaidStatus(paidStatus);
            createdCustomerPaymentModel = this.update(requestedCustomerPaymentModel);

            customerDuePaymentHistoryModel = new CustomerDuePaymentHistoryModel();
            customerDuePaymentHistoryModel.setInvoiceNo(createdCustomerPaymentModel.getInvoiceNo());
            customerDuePaymentHistoryModel.setPaidAmount(currentPayment);
            customerDuePaymentHistoryModel.setPaymentDate(new Date());
            this.customerDuePaymentHistoryService.save(customerDuePaymentHistoryModel);


            if (createdCustomerPaymentModel != null) {
                responseMessage = this.buildResponseMessage(createdCustomerPaymentModel);
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Customer payment update successfully";
                //this.commit();
            } else {
                responseMessage = this.buildResponseMessage();
                responseMessage.httpStatus = HttpStatus.CONFLICT.value();
                responseMessage.message = "Failed to get Customer payment update !!!";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("updateCustomerPayment -> save got exception");
        }
        return responseMessage;
    }

}
