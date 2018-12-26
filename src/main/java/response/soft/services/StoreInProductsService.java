package response.soft.services;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import response.soft.model.StockModel;
import response.soft.model.StoreInProductsModel;
import response.soft.model.view.StoreInProductsViewModel;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StoreInProductsService extends BaseService<StoreInProduct> {

    private static final Logger log = LoggerFactory.getLogger(StoreInProductsService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(StoreInProduct.class);
        Core.runTimeModelType.set(StoreInProductsModel.class);
    }

    @Autowired
    private StockService stockService;

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

            //StoreInProductsModel storeInProductsModel;

            Integer quantity;
            //Double unitPrice;
            for (StoreInProductsViewModel storeInProductsViewModel : storeInProductsViewModelList) {

                quantity = storeInProductsViewModel.getQuantity();

                for(int i=0; i<quantity; i++){
                    this.saveStoreInProductsModel(savedStockModel.getId(), storeInProductsViewModel);
                }


               // if(quantity==1) {

                   // this.saveStoreInProductsModel(savedStockModel.getId(), storeInProductsViewModel);

                  /*  storeInProductsModel = new StoreInProductsModel();
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
        StoreInProductsModel storeInProductsModel;
        storeInProductsModel = new StoreInProductsModel();
        storeInProductsModel.setStockId(stockId);
        storeInProductsModel.setStoreId(storeInProductsViewModel.getStoreId());
        storeInProductsModel.setProductId(storeInProductsViewModel.getProductId());
        storeInProductsModel.setVendorId(storeInProductsViewModel.getVendorId());
        storeInProductsModel.setPrice(storeInProductsViewModel.getPrice());
        storeInProductsModel.setProductStatus(InventoryEnum.ProductStatus.AVAILABLE.get());
        storeInProductsModel.setEntryDate(storeInProductsViewModel.getEntryDate());
        storeInProductsModel.setManufacturingDate(storeInProductsViewModel.getManufacturingDate());
        storeInProductsModel.setExpirationDate(storeInProductsViewModel.getExpirationDate());
        this.save(storeInProductsModel);
    }

    public ResponseMessage updateStoreInProducts(RequestMessage requestMessage) {
        ResponseMessage responseMessage = null;
        StoreInProductsModel requestedStoreInProductsModel,
                searchDuplicateStockModel,
                oldStoreInProductsModel,
                updatedStoreInProductsModel;
        List<StoreInProductsModel> foundDuplicateStoreInProductsModelList;
        int countPropertyValueDifference;
        int acceptedUpdatePropertyDifference = 3;
        //byte[] imageByte;
        try {


            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(StoreInProductsModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/


            requestedStoreInProductsModel = Core.processRequestMessage(requestMessage, StoreInProductsModel.class);

            // retrieved old store to update created and created date.
            oldStoreInProductsModel = this.getByIdActiveStatus(requestedStoreInProductsModel.getId());


            // "name", "category_id", "model_no", "brand", "barcode"
            // search for duplicate product

            if (requestedStoreInProductsModel != null && !ObjectUtils.isEmpty(requestedStoreInProductsModel)) {
                searchDuplicateStockModel = new StoreInProductsModel();
//                searchDuplicateStockModel.setName(requestedStockModel.getName());
//                searchDuplicateStockModel.setCategoryId(requestedStockModel.getCategoryId());
//                searchDuplicateStockModel.setBrandId(requestedStockModel.getBrandId());
//                searchDuplicateStockModel.setModelNo(requestedStockModel.getModelNo());
//                searchDuplicateStockModel.setBarcode(requestedStockModel.getBarcode());
                foundDuplicateStoreInProductsModelList = this.getAllByConditionWithActive(searchDuplicateStockModel);

                if (foundDuplicateStoreInProductsModelList.size() == 0) {
                    updatedStoreInProductsModel = this.update(requestedStoreInProductsModel, oldStoreInProductsModel);
                    responseMessage = this.buildResponseMessage(updatedStoreInProductsModel);
                    responseMessage.httpStatus = HttpStatus.OK.value();
                    responseMessage.message = "Successfully StoreInProduct updated";
                    //this.commit();
                    return responseMessage;
                }

                if (foundDuplicateStoreInProductsModelList.size() > 0) {

                    countPropertyValueDifference = Core.comparePropertyValueDifference(requestedStoreInProductsModel, oldStoreInProductsModel);
                    if (countPropertyValueDifference == acceptedUpdatePropertyDifference
                            || countPropertyValueDifference < acceptedUpdatePropertyDifference) {
                        updatedStoreInProductsModel = this.update(requestedStoreInProductsModel, oldStoreInProductsModel);
                        responseMessage = this.buildResponseMessage(updatedStoreInProductsModel);
                        responseMessage.httpStatus = HttpStatus.OK.value();
                        responseMessage.message = "Successfully StoreInProduct updated";
                        return responseMessage;
                    } else {
                        responseMessage = this.buildResponseMessage(requestedStoreInProductsModel);
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
        StoreInProductsModel storeInProductsModel;
        Integer numberOfDeletedRow;
        try {
            //StoreInProductsModel = Core.processRequestMessage(requestMessage, StoreInProductsModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(StoreInProductsModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/
            storeInProductsModel = this.getById(id);
            //StoreInProductsModel = this.softDelete(StoreInProductsModel);

            numberOfDeletedRow = this.deleteSoft(id);

            responseMessage = this.buildResponseMessage(numberOfDeletedRow);

            if (storeInProductsModel != null) {
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
        StoreInProductsModel storeInProductsModel;
        //String base64textString[];

        try {
            storeInProductsModel = this.getByIdActiveStatus(id);
            //storeInProductsModel.setImage(Base64.decodeBase64(storeInProductsModel.getImage()));

            responseMessage = buildResponseMessage(storeInProductsModel);

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
        List<StoreInProductsModel> list;
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

                list = this.executeHqlQuery(queryBuilderString.toString(), StoreInProductsModel.class, SqlEnum.QueryType.Join.get());

            } else {
                list = this.getAll();
            }



            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(StoreInProductsModel);
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


}
