package response.soft.services;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import response.soft.appenum.InventoryEnum;
import response.soft.appenum.SqlEnum;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.core.datatable.model.DataTableRequest;
import response.soft.entities.StoreInProduct;
import response.soft.entities.view.SalesProductView;
import response.soft.model.StockModel;
import response.soft.model.StoreInProductModel;
import response.soft.model.view.SalesProductViewModel;
import response.soft.model.view.StoreInProductsViewModel;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class StoreInProductService extends BaseService<StoreInProduct> {

    private static final Logger log = LoggerFactory.getLogger(StoreInProductService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(StoreInProduct.class);
        Core.runTimeModelType.set(StoreInProductModel.class);
    }

    @Autowired
    private StockService stockService;

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Transactional
    public ResponseMessage saveStoreInProducts(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        List<StoreInProductsViewModel> storeInProductsViewModelList;
        StockModel savedStockModel = null;

        try {
            String jsonString = Core.jsonMapper.writeValueAsString(requestMessage.data);
            //storeInProductsViewModels=  Core.gson.fromJson(jsonString, StoreInProductsViewModel[].class);


            storeInProductsViewModelList = jsonMapper.readValue(
                    jsonString, new TypeReference<List<StoreInProductsViewModel>>() {
                    });


            //storeInProductsViewModelList = Core.processRequestMessage(requestMessage, storeInProductsViewModels.getClass());


            Map<UUID, Map<UUID, List<StoreInProductsViewModel>>> groupByStoreIdAndProductId =
                    storeInProductsViewModelList.stream()
                            .collect(
                                    Collectors.groupingBy(StoreInProductsViewModel::getStoreId,
                                            Collectors.groupingBy(StoreInProductsViewModel::getProductId))
                            );


            Map<UUID, Map<UUID, List<StoreInProductsViewModel>>> groupByStoreIdMapCollection = new HashMap<>();
            List<Map<UUID, List<StoreInProductsViewModel>>> groupByStoreId = new ArrayList<>();
            //get store group
            for (Map.Entry<UUID, Map<UUID, List<StoreInProductsViewModel>>> entry : groupByStoreIdAndProductId.entrySet()) {
                Map<UUID, List<StoreInProductsViewModel>> value = entry.getValue();
                groupByStoreId.add(value);
                groupByStoreIdMapCollection.put(entry.getKey(), entry.getValue());
            }

            for (Map<UUID, List<StoreInProductsViewModel>> storeProduct : groupByStoreId) {
                for (Map.Entry<UUID, List<StoreInProductsViewModel>> store : storeProduct.entrySet()) {
                    UUID mapKey = store.getKey();

                    List<StoreInProductsViewModel> storeInProductsViewModels = store.getValue();
                    System.out.println(mapKey);

                    Double price, totalPrice = 0d, unitPrice;
                    Integer totalQuantity = 0, quantity;
                    UUID storeId = null, productId = null;

                    for (StoreInProductsViewModel productByStore : storeInProductsViewModels) {
                        storeId = productByStore.getStoreId();
                        productId = productByStore.getProductId();

                        price = productByStore.getPrice();
                        quantity = productByStore.getQuantity();
                        totalQuantity += quantity;
                        totalPrice += price * quantity;
                        //System.out.println(productByStore);
                    }
                    unitPrice = totalPrice / totalQuantity;
                    StockModel stockModel = new StockModel();
                    stockModel.setStoreId(storeId);
                    stockModel.setProductId(productId);
                    stockModel.setInOut(InventoryEnum.Stock.STOCK_IN.get());
                    stockModel.setQuantity(totalQuantity);
                    stockModel.setUnitPrice(unitPrice);
                    stockModel.setTotal(totalPrice);
                    stockModel.setDate(new Date());
                    savedStockModel = this.stockService.save(stockModel);
                }
            }

            //StoreInProductModel storeInProductsModel;

            Integer quantity;
            //Double unitPrice;
            for (StoreInProductsViewModel storeInProductsViewModel : storeInProductsViewModelList) {

                quantity = storeInProductsViewModel.getQuantity();

                for(int i=0; i<quantity; i++){
                    this.saveStoreInProductsModel(savedStockModel.getId(), storeInProductsViewModel);
                }


               // if(quantity==1) {

                   // this.saveStoreInProductsModel(savedStockModel.getId(), storeInProductsViewModel);

                  /*  storeInProductsModel = new StoreInProductModel();
                    storeInProductsModel.setStockId(savedStockModel.getId());
                    storeInProductsModel.setStoreId(storeInProductsViewModel.getStoreId());
                    storeInProductsModel.setProductId(storeInProductsViewModel.getProductId());
                    storeInProductsModel.setVendorId(storeInProductsViewModel.getVendorId());
                    storeInProductsModel.setPrice(storeInProductsViewModel.getPrice());
                    storeInProductsModel.setProductStatus(InventoryEnum.ProductStatus.AVAILABLE.get());
                    storeInProductsModel.setEntryDate(storeInProductsViewModel.getEntryDate());
                    storeInProductsModel.setManufacturingDate(storeInProductsViewModel.getManufacturingDate());
                    storeInProductsModel.setExpirationDate(storeInProductsViewModel.getExpirationDate());

                    this.save(storeInProductsModel);*/

              /*
                }else {
                    for(int i=0; i<quantity; i++){
                        this.saveStoreInProductsModel(savedStockModel.getId(), storeInProductsViewModel);

                    }
                }*/



            }


            responseMessage = this.buildResponseMessage();
            responseMessage.httpStatus=HttpStatus.CREATED.value();
            responseMessage.message="Products successfully Entered into Stock";

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("saveStock -> save got exception");
        }
        return responseMessage;
    }

    private void saveStoreInProductsModel(UUID stockId, StoreInProductsViewModel storeInProductsViewModel) throws Exception {
        StoreInProductModel storeInProductModel;
        storeInProductModel = new StoreInProductModel();
        storeInProductModel.setStockId(stockId);
        storeInProductModel.setStoreId(storeInProductsViewModel.getStoreId());
        storeInProductModel.setProductId(storeInProductsViewModel.getProductId());
        storeInProductModel.setVendorId(storeInProductsViewModel.getVendorId());
        storeInProductModel.setPrice(storeInProductsViewModel.getPrice());
        storeInProductModel.setSerialNo(storeInProductsViewModel.getSerialNo());
        storeInProductModel.setProductStatus(InventoryEnum.ProductStatus.AVAILABLE.get());
        storeInProductModel.setEntryDate(storeInProductsViewModel.getEntryDate());
        storeInProductModel.setManufacturingDate(storeInProductsViewModel.getManufacturingDate());
        storeInProductModel.setExpirationDate(storeInProductsViewModel.getExpirationDate());
        this.save(storeInProductModel);
    }

    public ResponseMessage updateStoreInProducts(RequestMessage requestMessage) {
        ResponseMessage responseMessage = null;
        StoreInProductModel requestedStoreInProductModel,
                searchDuplicateStockModel,
                oldStoreInProductModel,
                updatedStoreInProductModel;
        List<StoreInProductModel> foundDuplicateStoreInProductModelList;
        int countPropertyValueDifference;
        int acceptedUpdatePropertyDifference = 3;
        //byte[] imageByte;
        try {


            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(StoreInProductModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/


            requestedStoreInProductModel = Core.processRequestMessage(requestMessage, StoreInProductModel.class);

            // retrieved old store to update created and created date.
            oldStoreInProductModel = this.getByIdActiveStatus(requestedStoreInProductModel.getId());


            // "name", "category_id", "model_no", "brand", "barcode"
            // search for duplicate product

            if (requestedStoreInProductModel != null && !ObjectUtils.isEmpty(requestedStoreInProductModel)) {
                searchDuplicateStockModel = new StoreInProductModel();
//                searchDuplicateStockModel.setName(requestedStockModel.getName());
//                searchDuplicateStockModel.setCategoryId(requestedStockModel.getCategoryId());
//                searchDuplicateStockModel.setBrandId(requestedStockModel.getBrandId());
//                searchDuplicateStockModel.setModelNo(requestedStockModel.getModelNo());
//                searchDuplicateStockModel.setBarcode(requestedStockModel.getBarcode());
                foundDuplicateStoreInProductModelList = this.getAllByConditionWithActive(searchDuplicateStockModel);

                if (foundDuplicateStoreInProductModelList.size() == 0) {
                    updatedStoreInProductModel = this.update(requestedStoreInProductModel, oldStoreInProductModel);
                    responseMessage = this.buildResponseMessage(updatedStoreInProductModel);
                    responseMessage.httpStatus = HttpStatus.OK.value();
                    responseMessage.message = "Successfully StoreInProduct updated";
                    //this.commit();
                    return responseMessage;
                }

                if (foundDuplicateStoreInProductModelList.size() > 0) {

                    countPropertyValueDifference = Core.comparePropertyValueDifference(requestedStoreInProductModel, oldStoreInProductModel);
                    if (countPropertyValueDifference == acceptedUpdatePropertyDifference
                            || countPropertyValueDifference < acceptedUpdatePropertyDifference) {
                        updatedStoreInProductModel = this.update(requestedStoreInProductModel, oldStoreInProductModel);
                        responseMessage = this.buildResponseMessage(updatedStoreInProductModel);
                        responseMessage.httpStatus = HttpStatus.OK.value();
                        responseMessage.message = "Successfully StoreInProduct updated";
                        return responseMessage;
                    } else {
                        responseMessage = this.buildResponseMessage(requestedStoreInProductModel);
                        responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                        responseMessage.message = "Failed to update StoreInProduct";
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

    public ResponseMessage deleteStoreInProducts(UUID id) {
        ResponseMessage responseMessage;
        StoreInProductModel storeInProductModel;
        Integer numberOfDeletedRow;
        try {
            //StoreInProductModel = Core.processRequestMessage(requestMessage, StoreInProductModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(StoreInProductModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/
            storeInProductModel = this.getById(id);
            //StoreInProductModel = this.softDelete(StoreInProductModel);

            numberOfDeletedRow = this.deleteSoft(id);

            responseMessage = this.buildResponseMessage(numberOfDeletedRow);

            if (storeInProductModel != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "StoreInProduct deleted successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to deleted StoreInProduct";
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

    public ResponseMessage getByStoreInProductsId(UUID id) {
        ResponseMessage responseMessage;
        StoreInProductModel storeInProductModel;
        //String base64textString[];

        try {
            storeInProductModel = this.getByIdActiveStatus(id);
            //storeInProductModel.setImage(Base64.decodeBase64(storeInProductModel.getImage()));

            responseMessage = buildResponseMessage(storeInProductModel);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Get requested StoreInProduct successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to requested StoreInProduct";
            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            //this.rollBack();
            ex.printStackTrace();
            log.error("getByStockId -> got exception");
        }

        return responseMessage;
    }

    public ResponseMessage getAllStoreInProducts(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        List<StoreInProductModel> list;
        DataTableRequest dataTableRequest;
        StringBuilder queryBuilderString;
        String searchKey = null;
        try {
            Core.processRequestMessage(requestMessage);
            dataTableRequest = requestMessage.dataTableRequest;

            if (dataTableRequest != null) {
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
                        .append("FROM StoreInProduct p ")
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
                        .append("AND p.status=" + SqlEnum.Status.Active.get());

                list = this.executeHqlQuery(queryBuilderString.toString(), StoreInProductModel.class, SqlEnum.QueryType.Join.get());

            } else {
                list = this.getAll();
            }



            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(StoreInProductModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/


            responseMessage = this.buildResponseMessage(list);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Get all StoreInProduct successfully";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get StoreInProduct";
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

    public ResponseMessage getProductListByIdentificationIds(RequestMessage requestMessage,UUID storeId, String barcode, String serialNo){
        ResponseMessage responseMessage;
        List<SalesProductViewModel> salesProductViewModelList =null;
        //List<StoreInProductModel> storeInProductModelList=null;
        //StoreInProductModel whereConditionStoreInProductModel;


        StringBuilder queryBuilder = new StringBuilder();
        String hql;
        try {
            Core.processRequestMessage(requestMessage);

            EntityManager entityManager = entityManagerFactory.createEntityManager();
            SessionFactory sessionFactory;
            Session session=null;

                sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
                session = sessionFactory.openSession();


            if(storeId!=null){

              queryBuilder.append("SELECT p.id AS productId, ")
                      .append("count(sip.productId) AS available, ")
                      .append("p.name AS productName, ")
                      .append("cat.name AS categoryName, ")
                      .append("brn.name AS brandName, ")
                      .append("p.modelNo, ")
                      .append("sip.price AS buyPrice, ")
                      .append("p.description, ")
                      .append("p.barcode, ")
                      .append("p.image ")
              .append("FROM StoreInProduct  sip ")
              .append("INNER JOIN Product p ON p.id = sip.productId ")
              .append("INNER JOIN Category cat ON p.categoryId = cat.id ")
              .append("INNER JOIN Brand brn ON p.brandId = brn.id ")
              .append("WHERE sip.productStatus = "+ InventoryEnum.ProductStatus.AVAILABLE.get() +" AND sip.storeId = '")
              .append(storeId+"' ");

              if(!StringUtils.isEmpty(barcode)  && !StringUtils.equals(barcode,"undefined"))
                queryBuilder.append("AND p.barcode = '" + barcode+"' ");

              if(!StringUtils.isEmpty(serialNo) && !StringUtils.equals(serialNo,"undefined"))
                  queryBuilder.append("AND sip.serialNo = '"+ serialNo+"' ");


                  queryBuilder.append("GROUP BY " +
                      "p.id , " +
                      "p.name, " +
                      "cat.name, " +
                      "brn.name, " +
                      "p.modelNo, " +
                      "sip.price, " +
                      "p.description, " +
                      "p.barcode, " +
                      "p.image, " +
                      "sip.storeId ")
              .append("ORDER BY sip.storeId ");


                //for the time being omit this portion of data table
               /*
                if (Core.shortColumnName.get() != null && Core.shortColumnName.get() != ""){
                  queryBuilder.append(", p."+ Core.shortColumnName.get() + " " + Core.shortDirection.get().toUpperCase());
                }

                */



              hql = queryBuilder.toString();

              List<SalesProductView> SalesProductViews;

              StringBuilder testStringBuilder = new StringBuilder();
                String testHql = "select v from SalesProductView v where v.storeId='"+storeId+"' ";
                testStringBuilder.append(testHql);

                if(!StringUtils.isEmpty(barcode)  && !StringUtils.equals(barcode,"undefined"))
                    testStringBuilder.append("AND v.barcode = '" + barcode+"' ");

                if(!StringUtils.isEmpty(serialNo) && !StringUtils.equals(serialNo,"undefined"))
                    testStringBuilder.append("AND v.serialNo = '"+ serialNo+"' ");




              SalesProductViews = session.createQuery(testStringBuilder.toString(), SalesProductView.class).getResultList();

              System.out.println(SalesProductViews);

              salesProductViewModelList = this.executeHqlQuery(hql,SalesProductViewModel.class,SqlEnum.QueryType.Join.get());
            }

            if(salesProductViewModelList !=null){
                responseMessage = buildResponseMessage(salesProductViewModelList);
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Retrieve all available product";
            }else {
                responseMessage = buildResponseMessage();
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "No available product in this store";
            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            //this.rollBack();
            ex.printStackTrace();
            log.error("getByStockId -> got exception");
        }

        return responseMessage;

    }

}
