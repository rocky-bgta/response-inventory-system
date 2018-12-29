package response.soft.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import response.soft.appenum.InventoryEnum;
import response.soft.appenum.SqlEnum;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.core.datatable.model.DataTableRequest;
import response.soft.entities.StoreOutProduct;
import response.soft.model.StoreOutProductModel;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class StoreSalesProductService extends BaseService<StoreOutProduct> {

    private static final Logger log = LoggerFactory.getLogger(StoreSalesProductService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(StoreOutProduct.class);
        Core.runTimeModelType.set(StoreOutProductModel.class);
    }

    public ResponseMessage saveStoreSalesProducts(RequestMessage requestMessage) {
        ResponseMessage responseMessage;// = new ResponseMessage();
        StoreOutProductModel stockInModel;
        //byte[] imageByte;
        StoreOutProductModel searchDuplicateStoreOutProductModel;
        List<StoreOutProductModel> foundDuplicateStoreOutProductModelList;
        try {
            stockInModel = Core.processRequestMessage(requestMessage, StoreOutProductModel.class);
            // "name", "category_id", "model_no", "brand", "barcode"
            // search for duplicate product

            /*
            if (stockInModel != null && !ObjectUtils.isEmpty(stockInModel)) {
                searchDuplicateStockModel = new StoreOutProductModel();
                searchDuplicateStockModel.setName(stockInModel.getName());
                searchDuplicateStockModel.setCategoryId(stockInModel.getCategoryId());
                searchDuplicateStockModel.setBrandId(stockInModel.getBrandId());
                searchDuplicateStockModel.setModelNo(stockInModel.getModelNo());
                searchDuplicateStockModel.setBarcode(stockInModel.getBarcode());

                foundDuplicateStock = this.getAllByConditionWithActive(searchDuplicateStockModel);
                if (foundDuplicateStock.size() != 0) {
                    responseMessage = this.buildResponseMessage();
                    responseMessage.httpStatus = HttpStatus.CONFLICT.value();
                    responseMessage.message = "Duplicate product found";
                    return responseMessage;
                }
            }*/

/*
            if (stockInModel.getBase64ImageString() != null && stockInModel.getBase64ImageString().length() > 0) {
                imageByte = Base64.decodeBase64(stockInModel.getBase64ImageString());
                stockInModel.setImage(imageByte);
            }*/

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(stockInModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            //stockInModel = Core.getTrimmedModel(stockInModel);
            stockInModel = this.save(stockInModel);
            //stockInModel.setBase64ImageString(new String(stockInModel.getImage()));
            responseMessage = this.buildResponseMessage(stockInModel);

            if (stockInModel != null) {
                responseMessage.httpStatus = HttpStatus.CREATED.value();
                responseMessage.message = "StoreOutProduct save successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to save StoreOutProduct";
                //this.rollBack();
            }
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
  
}
