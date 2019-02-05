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
        Double totalStockAmount=0D;
        String availableStock;
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

            availableStock=" SELECT " +
                    " v.store_name,"+
                    " v.store_id," +
                    " v.category_id," +
                    " v.category_name," +
                    " v.product_id," +
                    " v.product_name," +
                    " v.model_no," +
                    " v.total_price," +
                    " v.available_qty" +
                    " FROM " +
                    " " +
                    " ( ";



            availableStock+="SELECT  stock_sum.store_id, stock_sum.store_name," +
                    "category.id AS category_id," +
                    "category.name AS category_name," +
                    "stock_sum.product_id," +
                    "product.name AS product_name," +
                    "product.model_no," +
                    "sum(stock_sum.in_total_price_sum) - sum(stock_sum.out_total_price_sum) AS total_price," +
                    "sum(stock_sum.in_qty_sum) - sum(stock_sum.out_qty_sum) AS available_qty " +
                    "FROM " +
                    "(SELECT " +
                    "store.name as store_name,"+
                    "stock.store_id," +
                    "stock.product_id," +
                    "stock.in_out," +
                    "case stock.in_out when 1 then sum(stock.total) else 0 end as in_total_price_sum," +
                    "case stock.in_out when 2 then sum(stock.total) else 0 end as out_total_price_sum," +
                    "case stock.in_out when 1 then sum(stock.quantity) else 0 end as in_qty_sum," +
                    "case stock.in_out when 2 then sum(stock.quantity) else 0 end as out_qty_sum," +
                    "stock.quantity," +
                    "stock.unit_price," +
                    "stock.total " +
                    "FROM stock stock " +
                    "INNER JOIN store store ON stock.store_id = store.id "+
                    "WHERE stock is not null ";





            //list = this.executeNativeQuery(availableStock,AvailableStockView.class,SqlEnum.QueryType.Select.get());


            //========= total stock price===============================
            queryBuilderForTotalStockPrice.append("SELECT (sum(stock_total.into_total) - sum(stock_total.out_total)) as total FROM (" )
                    .append("SELECT stock.store_id, ")
                    .append("CASE stock.in_out WHEN 1 THEN SUM(stock.total) ELSE 0 END As into_total, ")
                    .append("CASE stock.in_out WHEN 2 THEN SUM(stock.total) ELSE 0 END AS out_total ")
                    .append("FROM stock stock ")
                    .append("INNER JOIN product product ON stock.product_id = product.id ")
                    .append("WHERE stock.store_id IS NOT NULL ");
            //=========================================================


            if(storeId!=null && !StringUtils.isEmpty(storeId)){
                availableStock+="AND stock.store_id ='" +storeId+ "' ";
                queryBuilderForTotalStockPrice.append("AND stock.store_id ='"+storeId+"' ");

            }

            if(!StringUtils.isEmpty(fromDate) && !StringUtils.isEmpty(toDate)){
                availableStock+="AND stock.date BETWEEN '" + fromDate+" 00:00:00' AND '"+toDate+" 23:59:59.999999' ";
                queryBuilderForTotalStockPrice.append("AND stock.date BETWEEN '" + fromDate+" 00:00:00' AND '"+toDate+" 23:59:59.999999' ");
            }

            availableStock+="GROUP BY " +
                    "store.name,"+
                    "stock.store_id," +
                    "stock.product_id," +
                    "stock.in_out," +
                    "stock.quantity," +
                    "stock.unit_price," +
                    "stock.total" +
                    ")" +
                    "stock_sum " +
                    "INNER JOIN product product ON product.id = stock_sum.product_id " +
                    "INNER JOIN category category ON product.category_id = category.id ";


            if(categoryId!=null &&  !StringUtils.isEmpty(categoryId)){
                //queryBuilderString.append("AND v.categoryId='"+categoryId+"' ");
                availableStock+="WHERE category.id = '"+categoryId+"' ";
                queryBuilderForTotalStockPrice.append("AND product.category_id ='"+categoryId+"' ");
                //queryBuilderForTotalStockPrice.append("AND v.categoryId='"+categoryId+"' ");
            }



            availableStock+="GROUP BY stock_sum.product_id, product.name, product.model_no, category.id, category.name, stock_sum.store_id, stock_sum.store_name ) v ";
            availableStock+="WHERE v.available_qty>0 ";



            //============ full text search ===========================================
            if ((searchKey != null && !StringUtils.isEmpty(searchKey))) {
               /* queryBuilderString
                        .append("AND ( ")
                        .append("lower(v.category_name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.store_name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.model_no) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.product_name) LIKE '%" + searchKey + "%' ")
                        .append("OR CAST(v.total_price AS VARCHAR ) LIKE '%" + searchKey + "%' ")
                        .append("OR CAST(v.available_qty AS VARCHAR) LIKE '%" + searchKey + "%' ")
                        .append(") ");*/

                availableStock+="AND ( ";
                availableStock+="lower(v.category_name) LIKE '%" + searchKey + "%' ";
                availableStock+="OR lower(v.store_name) LIKE '%" + searchKey + "%' ";
                availableStock+="OR lower(v.model_no) LIKE '%" + searchKey + "%' ";
                availableStock+="OR lower(v.product_name) LIKE '%" + searchKey + "%' ";
                availableStock+="OR CAST(v.total_price AS VARCHAR ) LIKE '%" + searchKey + "%' ";
                availableStock+="OR CAST(v.available_qty AS VARCHAR) LIKE '%" + searchKey + "%' ";
                availableStock+=") ";
            }
            //============ full text search ===========================================






            //list = this.executeHqlQuery(queryBuilderString.toString(),AvailableStockView.class,SqlEnum.QueryType.View.get());

            //totalStockAmount = list.stream().mapToDouble(AvailableStockView::getTotalPrice).sum();


            list = this.executeNativeQuery(availableStock,AvailableStockView.class,SqlEnum.QueryType.Select.get());


            responseMessage = this.buildResponseMessage(list);

            if(list.size()>0) {


                queryBuilderForTotalStockPrice.append("GROUP BY stock.in_out, stock.store_id ")
                        .append(") stock_total");

                Core.resetPaginationVariable();
                totalStockAmount = this.executeNativeQuery(queryBuilderForTotalStockPrice.toString(), Double.class, SqlEnum.QueryType.GetOne.get()).get(0);
                //totalStockAmount = this.executeNativeQuery(queryBuilderForTotalStockPrice.toString(), Total.class, SqlEnum.QueryType.Select.get()).get(0);
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
