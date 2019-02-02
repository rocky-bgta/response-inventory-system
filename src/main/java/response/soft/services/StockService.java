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
import response.soft.entities.Stock;
import response.soft.entities.view.AvailableStockView;
import response.soft.model.StockModel;
import response.soft.model.view.StockViewModel;

import java.util.List;
import java.util.UUID;

@Service
public class StockService extends BaseService<Stock> {

    private static final Logger log = LoggerFactory.getLogger(StockService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(Stock.class);
        Core.runTimeModelType.set(StockModel.class);
    }

    public ResponseMessage saveStock(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        StockModel stockModel;
        try {
            stockModel = Core.processRequestMessage(requestMessage, StockModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            // search for duplicate product

            /*if (vendorModel != null && !ObjectUtils.isEmpty(vendorModel)) {
                responseMessage = this.checkDuplicateStore(vendorModel);
                if (responseMessage != null)
                    return responseMessage;
            }*/


            stockModel = this.save(stockModel);
            responseMessage = this.buildResponseMessage(stockModel);

            if (stockModel != null) {
                responseMessage.httpStatus = HttpStatus.CREATED.value();
                responseMessage.message = "Stock save successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to save Stock";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage("Internal server error");
            ex.printStackTrace();
            //this.rollBack();
            log.error("saveStock -> save got exception");
        }
        return responseMessage;
    }

    public ResponseMessage updateStock(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        StockModel requestedStockModel, vendorSearchCondition,oldStockModel;
        List<StockModel> vendorModelList;
        try {
            requestedStockModel = Core.processRequestMessage(requestMessage, StockModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            responseMessage = this.buildResponseMessage(requestedStockModel);

            // retrieved old vendor to update created and created date.
            oldStockModel = this.getByIdActiveStatus(requestedStockModel.getId());



            vendorSearchCondition = new StockModel();
            //vendorSearchCondition.setName(vendorModel.getName());
            vendorModelList = this.getAllByConditionWithActive(vendorSearchCondition);
            if (vendorModelList.size() == 0) {
                requestedStockModel = this.update(requestedStockModel,oldStockModel);
                if (requestedStockModel != null) {
                    responseMessage.message = "Stock update successfully!";
                    responseMessage.httpStatus = HttpStatus.OK.value();
                    Core.commitTransaction();
                    return responseMessage;
                    //this.commit();
                }
            }


           /* if(vendorModelList.size()>0){
                if(StringUtils.equals(vendorModelList.get(0).getName(), vendorModel.getName())){
                    oldStock = vendorModelList.get(0);
                    vendorModel = this.update(vendorModel,oldStock);
                    if (vendorModel != null) {
                        responseMessage.message = "Stock update successfully!";
                        responseMessage.httpStatus = HttpStatus.OK.value();
                        //this.commit();
                    }
                }else {
                    responseMessage.httpStatus = HttpStatus.CONFLICT.value();
                    responseMessage.message = "Same Stock name already exist";
                    //this.rollBack();
                }
            }*/


        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            Core.rollBackTransaction();
            log.error("updateStock -> got exception");
        }
        return responseMessage;
    }


    public ResponseMessage deleteStock(UUID id) {
        ResponseMessage responseMessage;
        StockModel vendorModel;
        Integer numberOfDeletedRow;
        try {
            //categoryModel = Core.processRequestMessage(requestMessage, StockModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/
            vendorModel=this.getById(id);
            //categoryModel = this.softDelete(categoryModel);

            numberOfDeletedRow=this.deleteSoft(id);

            responseMessage = this.buildResponseMessage(numberOfDeletedRow);

            if (vendorModel != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Stock deleted successfully!";
                Core.commitTransaction();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to deleted Stock";
                Core.rollBackTransaction();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            Core.rollBackTransaction();
            log.error("deleteStock -> got exception");
        }
        return responseMessage;
    }

    public ResponseMessage getByStockId(UUID id) {
        ResponseMessage responseMessage;
        StockModel vendorModel;

        try {
            vendorModel=this.getByIdActiveStatus(id);

            responseMessage = buildResponseMessage(vendorModel);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Get requested Stock successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to requested Stock";
            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            //this.rollBack();
            ex.printStackTrace();
            log.error("getByStockId -> got exception");
        }

        return responseMessage;
    }


    public ResponseMessage getAllStock(RequestMessage requestMessage, String storeId, String categoryId, String productId) {
        ResponseMessage responseMessage;
        List<AvailableStockView> list;
        StockViewModel stockViewModel;
        String searchKey;
        String fromDate=null,toDate=null;
        StringBuilder queryBuilderString =new StringBuilder();
        StringBuilder queryBuilderForTotalStockPrice= new StringBuilder();
        //String joinQuery;
        Double totalStockAmount=0D;
        try {
            stockViewModel =  Core.processRequestMessage(requestMessage,StockViewModel.class);
            if(stockViewModel!=null){
                if(stockViewModel.getFromDate()!=null)
                    fromDate = Core.dateFormat.format(stockViewModel.getFromDate());
                if(stockViewModel.getToDate()!=null)
                    toDate = Core.dateFormat.format(stockViewModel.getToDate());
            }

            searchKey = Core.dataTableSearchKey.get();

            if(searchKey!=null && !StringUtils.equals(searchKey,"string")) {
                searchKey = searchKey.trim().toLowerCase();
            }

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            //========= total stock price===============================
            //queryBuilderForTotalStockPrice.append("SELECT SUM(v.totalPrice) FROM AvailableStockView v WHERE v.availableQty>0 ");
            queryBuilderForTotalStockPrice.append("SELECT SUM(v.totalPrice) ")
                    .append("FROM AvailableStockView v ")
                    .append("INNER JOIN Stock stock ON stock.productId = v.productId ")
                    .append("WHERE v.availableQty>0 ");
            //=========================================================

            /*select v.categoryId,
                    v.categoryName,
                    v.productId,
                    v.productName,
                    v.modelNo,
                    sum(v.totalPrice) as totalPrice,
            sum(v.availableQty) as availableQty
            from AvailableStockView v
            inner join Stock stock on v.productId = stock.productId
            where stock.storeId = '5f748c8c-0a8a-4148-87b8-bd5afe18a501'
            and v.availableQty>0
            group by
            v.categoryId,
                    v.categoryName,
                    v.productId,
                    v.productName,
                    v.modelNo,
                    v.totalPrice,
                    v.availableQty*/





            queryBuilderString.append("SELECT v.categoryId, ")
                    .append("v.categoryName, ")
                    .append("v.productId, ")
                    .append("v.productName, ")
                    .append("v.modelNo, ")
                    .append("sum(v.totalPrice) as totalPrice, ")
                    .append("sum(v.availableQty) as availableQty ")
                    .append("FROM AvailableStockView v ")
                    .append("INNER JOIN Stock stock ON stock.productId = v.productId ")
                    .append("WHERE v.availableQty>0 ");


            //joinQuery = queryBuilderString.toString();


            if(storeId!=null && !StringUtils.isEmpty(storeId)){
                queryBuilderString.append("AND stock.storeId ='"+storeId+"' ");
                queryBuilderForTotalStockPrice.append("AND stock.storeId ='"+storeId+"' ");

            }

            if(categoryId!=null &&  !StringUtils.isEmpty(categoryId)){
                queryBuilderString.append("AND v.categoryId='"+categoryId+"' ");
                queryBuilderForTotalStockPrice.append("AND v.categoryId='"+categoryId+"' ");
            }

            if(productId!=null &&  !StringUtils.isEmpty(productId)){
                queryBuilderString.append("AND v.productId='"+productId+"' ");
                queryBuilderForTotalStockPrice.append("AND v.productId='"+productId+"' ");
            }

            if(!StringUtils.isEmpty(fromDate) && !StringUtils.isEmpty(toDate)){
                queryBuilderString.append("AND stock.date BETWEEN '" + fromDate+" 00:00:00' AND '"+toDate+" 23:59:59.999999'");
                queryBuilderForTotalStockPrice.append("AND stock.date BETWEEN '" + fromDate+" 00:00:00' AND '"+toDate+" 23:59:59.999999'");
            }

            //============ full text search ===========================================

            if ((searchKey != null && !StringUtils.isEmpty(searchKey))) {
                queryBuilderString
                        .append("AND ( ")
                        .append("lower(v.categoryName) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.modelNo) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.productName) LIKE '%" + searchKey + "%' ")
                        .append("OR CAST(v.totalPrice AS string) LIKE '%" + searchKey + "%' ")
                        .append("OR CAST(v.availableQty AS string) LIKE '%" + searchKey + "%' ")
                        .append(") ");

                //============ full text search ===========================================
            }

         /*   group by
            v.categoryId,
                    v.categoryName,
                    v.productId,
                    v.productName,
                    v.modelNo,
                    v.totalPrice,
                    v.availableQty*/

            queryBuilderString.append(" group by ")
                    .append("v.categoryId, ")
                    .append("v.categoryName, ")
                    .append("v.productId, ")
                    .append("v.productName, ")
                    .append("v.modelNo, ")
                    .append("v.totalPrice, ")
                    .append("v.availableQty");


            list = this.executeHqlQuery(queryBuilderString.toString(),AvailableStockView.class,SqlEnum.QueryType.Join.get());

            responseMessage = this.buildResponseMessage(list);

            if(list.size()>0) {
                Core.resetPaginationVariable();
                totalStockAmount = this.executeHqlQuery(queryBuilderForTotalStockPrice.toString(), Double.class, SqlEnum.QueryType.View.get()).get(0);
            }

            stockViewModel = new StockViewModel();
            stockViewModel.setAvailableStockViewList(list);
            stockViewModel.setTotalStockProductPrice(totalStockAmount);

            responseMessage.data=stockViewModel;

            /* else {
                //queryBuilderString.setLength(0);
                //queryBuilderString.append(joinQuery + " WHERE v.availableQty>0 ");
                if(!StringUtils.isEmpty(fromDate) && !StringUtils.isEmpty(toDate)){
                    queryBuilderString.append("AND stock.date BETWEEN '" + fromDate+" 00:00:00' AND '"+toDate+" 23:59:59.999999'");
                }
                list = this.executeHqlQuery(queryBuilderString.toString(),AvailableStockView.class,SqlEnum.QueryType.View.get());
            }*/



            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get all Stock successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get Stock";
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            log.error("getAllStock -> save got exception");
        }
        return responseMessage;
    }
}
