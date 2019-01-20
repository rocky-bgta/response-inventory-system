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
import response.soft.entities.InvoiceHistory;
import response.soft.model.InvoiceHistoryModel;
import response.soft.model.view.InvoiceHistoryViewModel;

import java.util.List;
import java.util.UUID;

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

    public ResponseMessage getInvoiceDetailsList(RequestMessage requestMessage, UUID customerId) {
        ResponseMessage responseMessage;
        List<InvoiceHistoryViewModel> list=null;
        String searchKey;
        StringBuilder queryBuilderString =new StringBuilder();
        try {
            this.resetPaginationVariable();
            Core.processRequestMessage(requestMessage);
            searchKey = Core.dataTableSearchKey.get();

            if(searchKey!=null && !StringUtils.equals(searchKey,"string")) {
                searchKey = searchKey.trim().toLowerCase();
            }

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            queryBuilderString.append("SELECT invh.id AS invoiceId, ")
                    .append("c.id AS customerId, ")
                    .append("c.name AS customerName, ")
                    .append("invh.invoiceNo, ")
                    .append("invh.grandTotal AS invoiceAmount, ")
                    .append("case cp.paidStatus " +
                            " when 1 then 'Paid'" +
                            " when 2 then 'Partial' " +
                            " when 3 then 'Due' end AS invoiceStatus, ")
                    .append("invh.date ")
                    .append("FROM InvoiceHistory invh ")
                    .append("INNER JOIN CustomerPayment cp ON invh.invoiceNo = cp.invoiceNo ")
                    .append("INNER JOIN Customer c ON cp.customerId = c.id");


            //============ full text search ===========================================

            if ((searchKey != null && !StringUtils.isEmpty(searchKey))|| customerId!=null) {

            /*    SELECT invh.id AS invoiceId,
                c.id AS customerId,
                c.name AS customerName,

                invh.invoiceNo,
                invh.grandTotal AS invoiceAmount,
                case cp.paidStatus when 0 then 'Paid' when 2 then 'Partial' when 3 then 'Due' end AS invoiceStatus
            FROM
                InvoiceHistory invh
                INNER JOIN CustomerPayment cp ON invh.invoiceNo = cp.invoiceNo
                INNER JOIN Customer c ON cp.customerId = c.id*/




                       /* .append("WHERE ")
                        .append("( ")
                        .append("lower(v.name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.phoneNo) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.email) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.address) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.description) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.description) LIKE '%" + searchKey + "%' ")
                        .append(") ");
                */

                        //.append("AND v.status="+SqlEnum.Status.Active.get());



              /*  queryBuilderString.append("SELECT v FROM AvailableStockView v WHERE v.availableQty>0 ");
                if(storeId!=null &&  storeId instanceof UUID){
                    queryBuilderString.append("AND v.storeId ='"+storeId+"' ");
                    //isWhereAdded=true;
                }*/




                //list = this.executeHqlQuery(queryBuilderString.toString(),AvailableStockView.class,SqlEnum.QueryType.View.get());
                //============ full text search ===========================================
            }else {
                //queryBuilderString.setLength(0);
                //queryBuilderString.append("SELECT v FROM AvailableStockView v WHERE v.availableQty>0");

                list = this.executeHqlQuery(queryBuilderString.toString(),InvoiceHistoryViewModel.class,SqlEnum.QueryType.Join.get());

            }

            responseMessage = this.buildResponseMessage(list);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get all Invoice Details successfully";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get Invoice Details";
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
