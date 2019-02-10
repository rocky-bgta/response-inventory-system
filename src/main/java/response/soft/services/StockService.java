package response.soft.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import response.soft.appenum.InventoryEnum;
import response.soft.appenum.SqlEnum;
import response.soft.appenum.StockEnum;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.entities.Stock;
import response.soft.entities.view.AvailableStockView;
import response.soft.model.StockModel;
import response.soft.model.StoreInProductModel;
import response.soft.model.view.StockViewModel;

import java.util.List;
import java.util.UUID;

@Service
public class StockService extends BaseService<Stock> {

    private static final Logger log = LoggerFactory.getLogger(StockService.class);

    @Autowired
    private StoreInProductService storeInProductService;

    @Autowired
    private StoreOutProductService storeOutProductService;

   /* @Autowired
    public StockService(StoreInProductService storeInProductService) {
        this.storeInProductService = storeInProductService;
    }*/

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
        StockViewModel stockViewModel;
        String findStockIdHql;
        UUID storeId, productId;
        List<UUID> stockIdList;
        StockModel whereConditionStockModel;
        StoreInProductModel whereConditionInProductModel;
        try {
            stockViewModel = Core.processRequestMessage(requestMessage, StockViewModel.class);



                storeId = stockViewModel.getStoreId();
                productId = stockViewModel.getProductId();


                findStockIdHql="SELECT DISTINCT " +
                        "stock.id  " +
                        "FROM " +
                        "Stock stock " +
                        "INNER JOIN StoreInProduct sip ON stock.id = sip.stockId " +
                        "WHERE " +
                        "sip.productStatus = 1 " +
                        "AND sip.productId = '" +productId+"' " +
                        "AND sip.storeId = '" + storeId +"'";


                stockIdList = this.executeHqlQuery(findStockIdHql,UUID.class,SqlEnum.QueryType.Select.get());

                for(UUID stockId: stockIdList){
                    whereConditionStockModel = new StockModel();
                    whereConditionStockModel.setId(stockId);
                    whereConditionStockModel.setInOut(StockEnum.STOCK_IN.get());
                    whereConditionStockModel.setProductId(productId);
                    whereConditionStockModel.setStoreId(storeId);

                    this.deleteByConditions(whereConditionStockModel);
                }

                for(UUID stockId: stockIdList){
                    whereConditionInProductModel = new StoreInProductModel();
                    whereConditionInProductModel.setStockId(stockId);
                    whereConditionInProductModel.setProductStatus(InventoryEnum.ProductStatus.AVAILABLE.get());
                    whereConditionInProductModel.setProductId(productId);
                    whereConditionInProductModel.setStoreId(storeId);

                    this.storeInProductService.deleteByConditions(whereConditionInProductModel);
                }





                //System.out.println(stockIdList);


               /*


                storeId = stockViewModel.getStoreId();
                productId = stockViewModel.getProductId();

                findStockHql="select stock from Stock stock " +
                        "WHERE stock.storeId = '" + storeId+ "' " +
                        "AND stock.productId = '" + productId+"' " +
                        "AND (stock.inOut = 2 OR stock.inOut = 1)";

                stockModelListForDelete = this.executeHqlQuery(findStockHql,StockModel.class,SqlEnum.QueryType.Select.get());

                findStoreInProductHql = "SELECT sip FROM StoreInProduct sip " +
                        "WHERE sip.storeId = '" + storeId+ "' " +
                        "AND sip.productId = '" + productId+"' " +
                        "AND (sip.productStatus = 2 OR sip.productStatus = 1)";


                storeInProductModelListForDelete = this.executeHqlQuery(findStoreInProductHql,StoreInProductModel.class,SqlEnum.QueryType.Select.get());


                findStoreOutProductHql = "SELECT sop FROM StoreOutProduct sop " +
                        "WHERE sop.storeId = '" + storeId+ "' " +
                        "AND sop.productId = '" + productId+"' ";

                storeOutProductModelListForDelete = this.executeHqlQuery(findStoreOutProductHql,StoreOutProductModel.class,SqlEnum.QueryType.Select.get());


                // delete store out product
                for(StoreOutProductModel deleteStoreOutProductModel: storeOutProductModelListForDelete){
                    this.storeOutProductService.delete(deleteStoreOutProductModel);
                }

                // delete store in product
                for(StoreInProductModel deleteInStoreInProductModel: storeInProductModelListForDelete){
                    this.storeInProductService.delete(deleteInStoreInProductModel);
                }

                // delete stock product
                for(StockModel deleteStockModel: stockModelListForDelete){
                    this.delete(deleteStockModel);
                }
*/

               /* for(SalesProductViewModel salesProductViewModel: salesProductViewModelList){
                    stockModel = new StockModel();
                    stockModel.setStoreId(storeId);
                    stockModel.setProductId(productId);
                    stockModel.setQuantity(salesProductViewModel.getAvailable());
                    stockModel.setUnitPrice(salesProductViewModel.getBuyPrice());
                    stockModel.setTotal(salesProductViewModel.getTotalPrice());
                    stockModel.setDate(new Date());
                    stockModel.setInOut(InventoryEnum.Stock.STOCK_IN.get());

                   savedStockModel= this.save(stockModel);


                    for(int i=0; i<salesProductViewModel.getAvailable(); i++){

                        *//*  storeInProductModel = storeInProductModelListForDelete.stream().filter(
                                x->x.getStoreId().equals(storeId)
                                && x.getProductId().equals(productId)
                                && x.getVendorId().equals(salesProductViewModel.getVendorId())).findFirst().get();

                        *//*

                        storeInProductModel = new StoreInProductModel();
                        storeInProductModel.setStockId(savedStockModel.getId());
                        storeInProductModel.setStoreId(storeId);
                        storeInProductModel.setProductId(productId);
                        storeInProductModel.setVendorId(salesProductViewModel.getVendorId());
                        storeInProductModel.setPrice(salesProductViewModel.getBuyPrice());
                        storeInProductModel.setProductStatus(InventoryEnum.ProductStatus.AVAILABLE.get());
                        storeInProductModel.setEntryDate(new Date());
                        this.storeInProductService.save(storeInProductModel);
                    }
                }
*/


               /* System.out.printf(storeInProductModelListForDelete.size()+"");

                System.out.printf(stockModelListForDelete.size()+"");*/




            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            responseMessage = this.buildResponseMessage(null);
            if(stockIdList.size()>0) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message="Stock Product Deleted successfully";
                Core.commitTransaction();
            }else {
                responseMessage.httpStatus = HttpStatus.CONFLICT.value();
                responseMessage.message="Failed to Deleted Product stock successfully";
            }




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

/*
            SELECT
            stock.ID as stock_id,
                    stock.store_id,
                    stock.product_id,
                    stock.in_out,
                    stock.quantity,
                    stock.total,
                    sip.id as sip_id
            FROM
            stock stock
            INNER JOIN store_in_product sip ON stock.ID = sip.stock_id
            WHERE
            sip.product_status = 1
            AND sip.product_id = 'f1f5cc1c-87e1-4375-b538-eb9cbd0eac60'
            AND sip.store_id = '5f748c8c-0a8a-4148-87b8-bd5afe18a501'*/

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
        //StringBuilder queryBuilderString =new StringBuilder();
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
                    "WHERE stock.status=1 ";





            //list = this.executeNativeQuery(availableStock,AvailableStockView.class,SqlEnum.QueryType.Select.get());


            //========= total stock price===============================
            queryBuilderForTotalStockPrice.append("SELECT (sum(stock_total.into_total) - sum(stock_total.out_total)) as total FROM (" )
                    .append("SELECT stock.store_id, ")
                    .append("CASE stock.in_out WHEN 1 THEN SUM(stock.total) ELSE 0 END As into_total, ")
                    .append("CASE stock.in_out WHEN 2 THEN SUM(stock.total) ELSE 0 END AS out_total ")
                    .append("FROM stock stock ")
                    .append("INNER JOIN product product ON stock.product_id = product.id ")
                    .append("WHERE stock.status=1 ");
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
