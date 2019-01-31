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
import response.soft.entities.SalesHistory;
import response.soft.entities.view.ProductSalesReportView;
import response.soft.model.SalesHistoryModel;
import response.soft.model.view.ProductSalesReportViewModel;

import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class ProductSalesService extends BaseService<SalesHistory> {
    private static final Logger log = LoggerFactory.getLogger(ProductSalesService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(SalesHistory.class);
        Core.runTimeModelType.set(SalesHistoryModel.class);
    }

    public ResponseMessage getProductSalesReport(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        ProductSalesReportViewModel productSalesReportViewModel;
        List<ProductSalesReportView> list;
        String searchKey;
        StringBuilder queryBuilderString =new StringBuilder();
        String fromDate, toDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.resetPaginationVariable();
            productSalesReportViewModel = Core.processRequestMessage(requestMessage,ProductSalesReportViewModel.class);

            fromDate = dateFormat.format(productSalesReportViewModel.getFromDate());
            toDate =  dateFormat.format(productSalesReportViewModel.getToDate());

            searchKey = Core.dataTableSearchKey.get();
            if(searchKey!=null && !StringUtils.equals(searchKey,"string")) {
                searchKey = searchKey.trim().toLowerCase();
            }

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/



            //============ full text search ===========================================

            if ((searchKey != null && !StringUtils.isEmpty(searchKey))) {

               /* queryBuilderString.append("SELECT v.id, ")
                        .append("v.name, ")
                        .append("v.phoneNo, ")
                        .append("v.email, ")
                        .append("v.address, ")
                        .append("v.description ")
                        .append("FROM Stock v ")
                        .append("WHERE ")
                        .append("( ")
                        .append("lower(v.name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.phoneNo) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.email) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.address) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.description) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.description) LIKE '%" + searchKey + "%' ")
                        .append(") ")
                        .append("AND v.status="+SqlEnum.Status.Active.get());

                */


                //Boolean isWhereAdded=false;
                queryBuilderString.append("SELECT v FROM ProductSalesReportView v");

                list = this.executeHqlQuery(queryBuilderString.toString(),ProductSalesReportView.class,SqlEnum.QueryType.View.get());
                //============ full text search ===========================================
            }else {
                queryBuilderString.setLength(0);
                queryBuilderString.append("SELECT v FROM ProductSalesReportView v ");

                if(fromDate!=null && toDate!=null){
                    queryBuilderString.append("WHERE v.date BETWEEN '" + fromDate+" 00:00:00' AND '"+toDate+" 23:59:59.999999'");
                }

                list = this.executeHqlQuery(queryBuilderString.toString(),ProductSalesReportView.class,SqlEnum.QueryType.View.get());
            }

            responseMessage = this.buildResponseMessage(list);

            if (list != null && list.size()>0) {
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get Product Sales Report successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get Product Sales Report";
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            log.error("getProductSalesReport -> save got exception");
        }
        return responseMessage;
    }
}
