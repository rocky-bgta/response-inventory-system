package response.soft.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import response.soft.Utils.AppUtils;
import response.soft.appenum.InventoryEnum;
import response.soft.appenum.SqlEnum;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.core.datatable.model.DataTableRequest;
import response.soft.entities.SalesHistory;
import response.soft.model.*;
import response.soft.model.view.ProductSalesViewModel;
import response.soft.model.view.SalesHistoryViewModel;
import response.soft.model.view.SalesProductViewModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class SalesHistoryService extends BaseService<SalesHistory> {

    private static final Logger log = LoggerFactory.getLogger(SalesHistoryService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(SalesHistory.class);
        Core.runTimeModelType.set(SalesHistoryModel.class);
    }

    @Autowired
    private StockService stockService;

    @Autowired
    private StoreInProductService storeInProductService;

    @Autowired
    private StoreOutProductService storeOutProductService;

    @Autowired
    private ProductSalesService productSalesService;

    @Autowired
    private InvoiceHistoryService invoiceHistoryService;

    @Autowired
    private CustomerPaymentService customerPaymentService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerDuePaymentHistoryService customerDuePaymentHistoryService;

    public ResponseMessage saveStoreSalesProducts(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        ProductSalesViewModel productSalesViewModel;
        List<SalesProductViewModel> salesProductViewModelList;

        List<StoreInProductModel> storeInProductModelList;
        StoreInProductModel whereConditionSIN;
        Integer salesQty;

        UUID storeId;
        UUID customerId;// = UUID.randomUUID() ;
        Integer salesMethod=null;
        String invoiceNo;
        InvoiceHistoryModel invoiceHistoryModel;
        CustomerPaymentModel customerPaymentModel;

        //===== stock variable ========================
        StockModel stockModel, whereConditionStockModel;
        Double unitPrice,totalPrice;
        //=============================================

        StoreOutProductModel storeOutProductModel,savedStoreOutProductModel;
        StoreInProductModel updatedStoreInProductModel;
        SalesHistoryModel salesHistoryModel;
        CustomerModel requestedCustomerModel=null,createdCustomerModel;
        Boolean isCustomerExist;
        InvoiceHistoryModel whereConditionInvoiceHistoryModel;
        List<InvoiceHistoryModel> invoiceHistoryModelList;
        CustomerDuePaymentHistoryModel customerDuePaymentHistoryModel;

        try {

            productSalesViewModel = Core.processRequestMessage(requestMessage, ProductSalesViewModel.class);

            salesProductViewModelList = productSalesViewModel.getSalesProductViewModelList();
            Date invoiceDate = new Date();
            Double paidAmount = productSalesViewModel.getPaidAmount();
            Double dueAmount = productSalesViewModel.getDueAmount();
            Double grandTotal = productSalesViewModel.getGrandTotal();
            invoiceNo = productSalesViewModel.getInvoiceNo();
            //storeId = productSalesViewModel.getStoreId();

            //check duplicate customer;
            if(productSalesViewModel.getCustomerModel() !=null) {
                requestedCustomerModel = productSalesViewModel.getCustomerModel();
                isCustomerExist = this.customerService.isCustomerAlreadyExist(requestedCustomerModel);
                if(isCustomerExist){
                    responseMessage = this.buildResponseMessage();
                    responseMessage.message="Duplicate customer information found!!!";
                    responseMessage.httpStatus = HttpStatus.IM_USED.value();
                    return responseMessage;
                }
            }
            //check duplicate customer;

            //check duplicate invoice no =====================================
            whereConditionInvoiceHistoryModel = new InvoiceHistoryModel();
            whereConditionInvoiceHistoryModel.setInvoiceNo(invoiceNo);
            invoiceHistoryModelList = this.invoiceHistoryService.getAllByConditionWithActive(whereConditionInvoiceHistoryModel);

            if(invoiceHistoryModelList!=null && invoiceHistoryModelList.size()>0){
                responseMessage = this.buildResponseMessage();
                responseMessage.message="Duplicate Invoice information found!!!";
                responseMessage.httpStatus = HttpStatus.IM_USED.value();
                return responseMessage;
            }
//check     duplicate invoice no =====================================


            if(productSalesViewModel.getCustomerId()!=null)
                customerId = productSalesViewModel.getCustomerId();
            else {
                createdCustomerModel =  this.customerService.save(requestedCustomerModel);
                customerId = createdCustomerModel.getId();
            }

            if(productSalesViewModel.getSalesMethod()!=null)
                salesMethod = productSalesViewModel.getSalesMethod();
            Integer paymentStatus;







            for(SalesProductViewModel salesProductViewModel: salesProductViewModelList){

                salesQty = salesProductViewModel.getSalesQty();
                storeId = salesProductViewModel.getStoreId();

                // =========== First update stock =========================================================
                whereConditionStockModel = new StockModel();
                whereConditionStockModel.setStoreId(storeId);
                whereConditionStockModel.setProductId(salesProductViewModel.getProductId());
                stockModel = this.stockService.getAllByConditionWithActive(whereConditionStockModel).get(0);
                unitPrice = stockModel.getUnitPrice();

                totalPrice = unitPrice*salesQty;

                stockModel = new StockModel();
                stockModel.setStoreId(storeId);
                stockModel.setProductId(salesProductViewModel.getProductId());
                stockModel.setInOut(InventoryEnum.Stock.STOCK_OUT.get());
                stockModel.setQuantity(salesQty);
                stockModel.setUnitPrice(unitPrice);
                stockModel.setDate(invoiceDate);
                stockModel.setTotal(totalPrice);
                this.stockService.save(stockModel);
                // =========== First update stock end =========================================================


                // get available required products from stock ==========================================
                whereConditionSIN = new StoreInProductModel();
                whereConditionSIN.setStoreId(storeId);
                whereConditionSIN.setProductId(salesProductViewModel.getProductId());
                whereConditionSIN.setProductStatus(InventoryEnum.ProductStatus.AVAILABLE.get());

                storeInProductModelList =
                        this.storeInProductService.getAllByConditionWithActive(whereConditionSIN,salesQty);
                //========================================================================================



                for(StoreInProductModel storeInProductModel: storeInProductModelList){

                    /* update store-in product to sold product */
                    StoreInProductModel changeProductStatusStoreInProductModel = storeInProductModel;
                    changeProductStatusStoreInProductModel.setProductStatus(InventoryEnum.ProductStatus.SOLD.get());
                    changeProductStatusStoreInProductModel.setSerialNo(salesProductViewModel.getSerialNo());
                    updatedStoreInProductModel = this.storeInProductService.update(changeProductStatusStoreInProductModel);


                    /* insert data to store out table */
                    storeOutProductModel = new StoreOutProductModel();
                    storeOutProductModel.setStockId(updatedStoreInProductModel.getStockId());
                    storeOutProductModel.setStoreId(updatedStoreInProductModel.getStoreId());
                    storeOutProductModel.setStoreInProductId(updatedStoreInProductModel.getId());
                    storeOutProductModel.setProductId(updatedStoreInProductModel.getProductId());
                    storeOutProductModel.setDate(new Date());
                    savedStoreOutProductModel = this.storeOutProductService.save(storeOutProductModel);

                    // insert data into sales history table
                    salesHistoryModel = new SalesHistoryModel();
                    salesHistoryModel.setStoreOutId(savedStoreOutProductModel.getId());
                    salesHistoryModel.setProductId(savedStoreOutProductModel.getProductId());
                    salesHistoryModel.setCustomerId(customerId);
                    salesHistoryModel.setBuyPrice(updatedStoreInProductModel.getPrice());
                    salesHistoryModel.setSalesPrice(salesProductViewModel.getSalesPrice());
                    salesHistoryModel.setSalesType(salesMethod);

                    if(salesProductViewModel.getDiscount()!=null)
                        salesHistoryModel.setDiscount(salesProductViewModel.getDiscount());

                    salesHistoryModel.setDate(invoiceDate);
                    salesHistoryModel.setSupportPeriodInMonth(salesProductViewModel.getSupportPeriodInMonth());
                    salesHistoryModel.setSerialNo(salesProductViewModel.getSerialNo());
                    salesHistoryModel.setInvoiceNo(invoiceNo);
                    this.productSalesService.save(salesHistoryModel);

                }

            }

            // insert data into invoice balance
            invoiceHistoryModel = new InvoiceHistoryModel();
            invoiceHistoryModel.setInvoiceNo(invoiceNo);
            invoiceHistoryModel.setPaidAmount(paidAmount);
            invoiceHistoryModel.setDueAmount(dueAmount);
            invoiceHistoryModel.setGrandTotal(grandTotal);
            invoiceHistoryModel.setDate(invoiceDate);
            this.invoiceHistoryService.save(invoiceHistoryModel);


            // insert data into customer payment
            customerPaymentModel = new CustomerPaymentModel();
            customerPaymentModel.setCustomerId(customerId);
            customerPaymentModel.setInvoiceNo(invoiceNo);
            customerPaymentModel.setPaidAmount(paidAmount);
            customerPaymentModel.setDueAmount(dueAmount);
            customerPaymentModel.setGrandTotal(grandTotal);

            paymentStatus = AppUtils.getPaymentStatus(paidAmount,grandTotal);

            customerPaymentModel.setPaidStatus(paymentStatus);

            customerPaymentModel.setInvoiceDate(invoiceDate);
            this.customerPaymentService.save(customerPaymentModel);

            // insert data into customer due payment history
            customerDuePaymentHistoryModel = new CustomerDuePaymentHistoryModel();
            customerDuePaymentHistoryModel.setInvoiceNo(invoiceNo);
            customerDuePaymentHistoryModel.setPaidAmount(paidAmount);
            customerDuePaymentHistoryModel.setPaymentDate(invoiceDate);
            this.customerDuePaymentHistoryService.save(customerDuePaymentHistoryModel);



            responseMessage = this.buildResponseMessage();
            responseMessage.httpStatus = HttpStatus.CREATED.value();
            responseMessage.message="Sales Invoice generated successfully";

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("saveStock -> save got exception");
        }
        return responseMessage;
    }

    public ResponseMessage updateStoreSalesProducts(RequestMessage requestMessage) {
        ResponseMessage responseMessage=null;
        StoreOutProductModel requestedStoreOutProductModel,
                searchDuplicateStockModel,
                oldStoreOutProductModel,
                updatedStoreOutProductModel;
        List<StoreOutProductModel> foundDuplicateStoreOutProductModelList;
        int countPropertyValueDifference;
        int acceptedUpdatePropertyDifference=3;
        //byte[] imageByte;
        try {


            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(StoreOutProductModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/


            requestedStoreOutProductModel = Core.processRequestMessage(requestMessage, StoreOutProductModel.class);

            // retrieved old store to update created and created date.
            oldStoreOutProductModel = this.getByIdActiveStatus(requestedStoreOutProductModel.getId());


            // "name", "category_id", "model_no", "brand", "barcode"
            // search for duplicate product

            if (requestedStoreOutProductModel != null && !ObjectUtils.isEmpty(requestedStoreOutProductModel)) {
                searchDuplicateStockModel = new StoreOutProductModel();
//                searchDuplicateStockModel.setName(requestedStockModel.getName());
//                searchDuplicateStockModel.setCategoryId(requestedStockModel.getCategoryId());
//                searchDuplicateStockModel.setBrandId(requestedStockModel.getBrandId());
//                searchDuplicateStockModel.setModelNo(requestedStockModel.getModelNo());
//                searchDuplicateStockModel.setBarcode(requestedStockModel.getBarcode());
                foundDuplicateStoreOutProductModelList = this.getAllByConditionWithActive(searchDuplicateStockModel);

                if (foundDuplicateStoreOutProductModelList.size() == 0) {
                    updatedStoreOutProductModel = this.update(requestedStoreOutProductModel, oldStoreOutProductModel);
                    responseMessage = this.buildResponseMessage(updatedStoreOutProductModel);
                    responseMessage.httpStatus = HttpStatus.OK.value();
                    responseMessage.message = "Successfully StoreOutProduct updated";
                    //this.commit();
                    return responseMessage;
                }

                if(foundDuplicateStoreOutProductModelList.size()>0){

                    countPropertyValueDifference = Core.comparePropertyValueDifference(requestedStoreOutProductModel, oldStoreOutProductModel);
                    if(countPropertyValueDifference==acceptedUpdatePropertyDifference
                            || countPropertyValueDifference<acceptedUpdatePropertyDifference){
                        updatedStoreOutProductModel = this.update(requestedStoreOutProductModel, oldStoreOutProductModel);
                        responseMessage = this.buildResponseMessage(updatedStoreOutProductModel);
                        responseMessage.httpStatus = HttpStatus.OK.value();
                        responseMessage.message = "Successfully StoreOutProduct updated";
                        return responseMessage;
                    }else {
                        responseMessage = this.buildResponseMessage(requestedStoreOutProductModel);
                        responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                        responseMessage.message = "Failed to update StoreOutProduct";
                        //this.rollBack();
                        return responseMessage;
                    }

                }
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("updateStock -> got exception");
        }
        return responseMessage;
    }

    public ResponseMessage deleteStoreSalesProducts(UUID id) {
        ResponseMessage responseMessage;
        StoreOutProductModel stockInModel;
        Integer numberOfDeletedRow;
        try {
            //StoreOutProductModel = Core.processRequestMessage(requestMessage, StoreOutProductModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(StoreOutProductModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/
            stockInModel = this.getById(id);
            //StoreOutProductModel = this.softDelete(StoreOutProductModel);

            numberOfDeletedRow = this.deleteSoft(id);

            responseMessage = this.buildResponseMessage(numberOfDeletedRow);

            if (stockInModel != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "StoreOutProduct deleted successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to deleted StoreOutProduct";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("deleteStock -> got exception");
        }
        return responseMessage;
    }

    public ResponseMessage getByStoreSalesProductsId(UUID id) {
        ResponseMessage responseMessage;
        StoreOutProductModel stockInModel;
        //String base64textString[];

        try {
            stockInModel = this.getByIdActiveStatus(id);
            //stockInModel.setImage(Base64.decodeBase64(stockInModel.getImage()));

            responseMessage = buildResponseMessage(stockInModel);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Get requested StoreOutProduct successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to requested StoreOutProduct";
            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            //this.rollBack();
            ex.printStackTrace();
            log.error("getByStockId -> got exception");
        }

        return responseMessage;
    }

    public ResponseMessage getAllStoreSalesProducts(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        List<StoreOutProductModel> list;
        DataTableRequest dataTableRequest;
        StringBuilder queryBuilderString;
        String searchKey=null;
        try {
            Core.processRequestMessage(requestMessage);
            dataTableRequest = requestMessage.dataTableRequest;

            if(dataTableRequest!=null) {
                searchKey = dataTableRequest.search.value;
                searchKey = searchKey.trim().toLowerCase();
            }

            if (dataTableRequest != null && !StringUtils.isEmpty(searchKey)) {
                //implement full-text search
                queryBuilderString = new StringBuilder();
                queryBuilderString.append("SELECT p.id, ")
                        .append("p.name, ")
                        .append("c.id, ")
                        .append("p.brandId, ")
                        .append("p.modelNo, ")
                        .append("CAST(p.price AS string), ")
                        .append("p.description, ")
                        .append("p.barcode, ")
                        .append("p.image ")
                        .append("FROM StoreOutProduct p ")
                        .append("LEFT JOIN Category c ON p.categoryId = c.id  ")
                        .append("LEFT JOIN Brand b ON p.brandId = b.id  ")
                        .append("WHERE ")
                        .append("( ")
                        .append("lower(p.name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(c.name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(b.name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(p.modelNo) LIKE '%" + searchKey + "%' ")
                        .append("OR CAST(p.price AS string) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(p.description) LIKE '%" + searchKey + "%' ")
                        .append(") ")
                        .append("AND p.status="+SqlEnum.Status.Active.get());

                list = this.executeHqlQuery(queryBuilderString.toString(), StoreOutProductModel.class, SqlEnum.QueryType.Join.get());

            } else {
                list = this.getAll();
            }



            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(StoreOutProductModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/


            responseMessage = this.buildResponseMessage(list);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Get all StoreOutProduct successfully";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get StoreOutProduct";
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

    public ResponseMessage getSalesHistoryByInvoiceNo(String invoiceNo) {
        ResponseMessage responseMessage;
        List<SalesHistoryViewModel> list;

        StringBuilder queryBuilderString;
        String searchKey;
        try {
            this.resetPaginationVariable();
             /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(StoreOutProductModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            //Core.processRequestMessage(requestMessage);
            searchKey = Core.dataTableSearchKey.get();

            if(searchKey!=null) {
                searchKey = searchKey.trim().toLowerCase();
            }


           /* SELECT
            sh.invoiceNo,
            s.id AS storeId,
            s.name AS storeName,
            sh.customerId,
            c.name AS customerName,
            sh.productId,
                    p.name as productName,
            sh.salesPrice

                    FROM
            SalesHistory sh
            INNER JOIN StoreOutProduct sop ON sh.storeOutId = sop.id
            INNER JOIN Store s ON sop.storeId = s.id
            INNER JOIN Customer c ON sh.customerId = c.id
            INNER JOIN Product p ON sh.productId = p.id*/
            queryBuilderString = new StringBuilder();
            queryBuilderString.append("SELECT ")
                    .append("sh.invoiceNo, ")
                    .append("s.id AS storeId, ")
                    .append("s.name AS storeName, ")
                    .append("sh.customerId, ")
                    .append("c.name AS customerName, ")
                    .append("sh.productId, ")
                    .append("p.name as productName, ")
                    .append("CAST(sh.salesPrice AS string) ")
                    .append("FROM  SalesHistory sh ")
                    .append("INNER JOIN StoreOutProduct sop ON sh.storeOutId = sop.id  ")
                    .append("INNER JOIN Store s ON sop.storeId = s.id  ")
                    .append("INNER JOIN Customer c ON sh.customerId = c.id  ")
                    .append("INNER JOIN Product p ON sh.productId = p.id  ")
            .append("WHERE sh.invoiceNo = '" + invoiceNo + "'" );

            if (searchKey != null && !StringUtils.isEmpty(searchKey)) {
                //implement full-text search



                      /*  .append("WHERE ")
                        .append("( ")
                        .append("lower(p.name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(c.name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(b.name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(p.modelNo) LIKE '%" + searchKey + "%' ")
                        .append("OR CAST(p.price AS string) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(p.description) LIKE '%" + searchKey + "%' ")
                        .append(") ")
                        .append("AND p.status="+SqlEnum.Status.Active.get());

                       */

                list = this.executeHqlQuery(queryBuilderString.toString(), SalesHistoryViewModel.class, SqlEnum.QueryType.Join.get());

            } else {
                list = this.executeHqlQuery(queryBuilderString.toString(), SalesHistoryViewModel.class, SqlEnum.QueryType.Join.get());
            }

            responseMessage = this.buildResponseMessage(list);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get all Sales History successfully";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get Sales History";
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
