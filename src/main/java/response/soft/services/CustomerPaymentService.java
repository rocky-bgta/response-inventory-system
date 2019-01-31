package response.soft.services;

import org.apache.commons.lang3.StringUtils;
import org.decimal4j.util.DoubleRounder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import response.soft.appenum.InventoryEnum;
import response.soft.appenum.SqlEnum;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.entities.CustomerPayment;
import response.soft.model.CustomerDuePaymentHistoryModel;
import response.soft.model.CustomerPaymentModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerPaymentService extends BaseService<CustomerPayment> {
    private static final Logger log = LoggerFactory.getLogger(CustomerPaymentService.class);

    private final CustomerDuePaymentHistoryService customerDuePaymentHistoryService;

    @Autowired
    public CustomerPaymentService(CustomerDuePaymentHistoryService customerDuePaymentHistoryService) {
        this.customerDuePaymentHistoryService = customerDuePaymentHistoryService;
    }

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(CustomerPayment.class);
        Core.runTimeModelType.set(CustomerPaymentModel.class);
    }

    public ResponseMessage getCustomerPaymentList(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        List<CustomerPaymentModel> list;
        String searchKey;
        StringBuilder queryBuilderString = new StringBuilder();
        try {
            Core.processRequestMessage(requestMessage);
            searchKey = Core.dataTableSearchKey.get();

            if (searchKey != null && !StringUtils.equals(searchKey, "string")) {
                searchKey = searchKey.trim().toLowerCase();
            }

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            //============ full text search ===========================================

            if ((searchKey != null && !StringUtils.isEmpty(searchKey))) {

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
                        .append("AND ( cp.paidStatus = " + InventoryEnum.PaymentStatus.PARTIAL.get())
                        .append(" OR cp.paidStatus = " + InventoryEnum.PaymentStatus.DUE.get() + " )");


                list = this.executeHqlQuery(queryBuilderString.toString(), CustomerPaymentModel.class, SqlEnum.QueryType.Join.get());
                //============ full text search ===========================================
            } else {
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
                        .append("WHERE cp.paidStatus = " + InventoryEnum.PaymentStatus.PARTIAL.get())
                        .append(" OR cp.paidStatus = " + InventoryEnum.PaymentStatus.DUE.get());

                list = this.executeHqlQuery(queryBuilderString.toString(), CustomerPaymentModel.class, SqlEnum.QueryType.Join.get());
            }


            if (list != null && list.size() > 0) {
                responseMessage = this.buildResponseMessage(list);
                responseMessage.data = list;
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get all Customer payment list successfully";

            } else {
                responseMessage = this.buildResponseMessage();
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get Customer payment list";
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
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
        Double grandTotal;
        CustomerDuePaymentHistoryModel customerDuePaymentHistoryModel;

        try {

             /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            requestedCustomerPaymentModel = Core.processRequestMessage(requestMessage, CustomerPaymentModel.class);
            grandTotal = requestedCustomerPaymentModel.getGrandTotal();
            dueAmount = requestedCustomerPaymentModel.getDueAmount();
            currentPayment = requestedCustomerPaymentModel.getCurrentPayment();

            dueAmount = dueAmount.doubleValue() - currentPayment.doubleValue();

            dueAmount = DoubleRounder.round(dueAmount.doubleValue(), 2);

            if (dueAmount.doubleValue() == 0) {
                paidStatus = InventoryEnum.PaymentStatus.PAID.get();
                requestedCustomerPaymentModel.setPaidAmount(grandTotal);
            } else {
                paidStatus = InventoryEnum.PaymentStatus.PARTIAL.get();
                requestedCustomerPaymentModel.setPaidAmount(currentPayment);
            }

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
                Core.commitTransaction();
            } else {
                responseMessage = this.buildResponseMessage();
                responseMessage.httpStatus = HttpStatus.CONFLICT.value();
                responseMessage.message = "Failed to get Customer payment update !!!";
                Core.rollBackTransaction();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            Core.rollBackTransaction();
            log.error("updateCustomerPayment -> save got exception");
        }
        return responseMessage;
    }

    public void payPreviousDueInvoice(UUID customerId, Double grandTotal, Double paidAmount) throws Exception {
        RequestMessage requestMessage = new RequestMessage();
        List<CustomerPaymentModel> dueInvoiceList;
        CustomerPaymentModel customerPaymentModelForPayment;
        Double excessAmount, remainAmountAfterInvoicePayment, paidAmountForInvoice;
        Double invoiceDueAmount;
        excessAmount = paidAmount.doubleValue() - grandTotal.doubleValue();
        try {

            if (excessAmount.doubleValue() > 0) {
                dueInvoiceList = this.dueInvoiceListByCustomerId(customerId);
                for (CustomerPaymentModel customerPaymentModel : dueInvoiceList) {
                    invoiceDueAmount = customerPaymentModel.getDueAmount();

                    if (excessAmount.doubleValue() == invoiceDueAmount.doubleValue()) {
                        customerPaymentModelForPayment = customerPaymentModel;
                        customerPaymentModelForPayment.setCurrentPayment(excessAmount);
                        requestMessage.data = customerPaymentModel;
                        this.updateCustomerPayment(requestMessage);
                        break;
                    } else if (excessAmount.doubleValue() > invoiceDueAmount.doubleValue()) {
                        remainAmountAfterInvoicePayment = excessAmount.doubleValue() - invoiceDueAmount.doubleValue();
                        //remainAmountAfterInvoicePayment = excessAmount.doubleValue() - paidAmountForInvoice.doubleValue();
                        excessAmount = remainAmountAfterInvoicePayment;

                        customerPaymentModelForPayment = customerPaymentModel;
                        customerPaymentModelForPayment.setCurrentPayment(invoiceDueAmount);
                        requestMessage.data = customerPaymentModel;
                        this.updateCustomerPayment(requestMessage);

                    } else if (excessAmount.doubleValue() < invoiceDueAmount.doubleValue()) {
                        customerPaymentModelForPayment = customerPaymentModel;
                        customerPaymentModelForPayment.setCurrentPayment(excessAmount);
                        requestMessage.data = customerPaymentModel;
                        this.updateCustomerPayment(requestMessage);
                        break;
                    }

                }
                Core.commitTransaction();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Core.rollBackTransaction();
            throw ex;
        }

    }

    public List<CustomerPaymentModel> dueInvoiceListByCustomerId(UUID customerId) throws Exception {
        List<CustomerPaymentModel> customerPaymentModelList;
        StringBuilder queryBuilder = new StringBuilder();

        /* SELECT
        cp.id,
                cp.customerId,
                cp.invoiceNo,
                cp.paidAmount,
                cp.dueAmount,
                cp.grandTotal,
                cp.paidStatus,
                cp.invoiceDate,
                cp.paymentDate,
                c.name AS customerName
        FROM
        CustomerPayment cp
        INNER JOIN Customer c ON cp.customerId = c.id
        WHERE
        cp.paidStatus = 2
        OR cp.paidStatus = 3
        AND c.id = '03cee2b6-601b-4347-bb81-69503f25b31f' order by cp.invoiceDate */
        try {
            queryBuilder.append("SELECT ")
                    .append("cp.id, ")
                    .append("cp.customerId, ")
                    .append("cp.invoiceNo, ")
                    .append("cp.paidAmount, ")
                    .append("cp.dueAmount, ")
                    .append("cp.grandTotal, ")
                    .append("cp.paidStatus, ")
                    .append("cp.invoiceDate, ")
                    .append("cp.paymentDate, ")
                    .append("c.name AS customerName ")
                    .append("FROM ")
                    .append("CustomerPayment cp ")
                    .append("INNER JOIN Customer c ON cp.customerId = c.id ")
                    .append("WHERE ")
                    .append("cp.paidStatus = 2 ")
                    .append("OR cp.paidStatus = 3 ")
                    .append("AND c.id = '" + customerId + "' ")
                    .append("ORDER BY cp.invoiceDate");

            customerPaymentModelList = this.executeHqlQuery(queryBuilder.toString(), CustomerPaymentModel.class, SqlEnum.QueryType.Join.get());


        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }

        return customerPaymentModelList;

    }

}
