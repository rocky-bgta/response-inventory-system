package response.soft.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import response.soft.appenum.SqlEnum;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.core.datatable.model.DataTableRequest;
import response.soft.entities.StoreSalesProducts;
import response.soft.model.StoreSalesProductsModel;

import java.util.List;
import java.util.UUID;

@Service
public class StockOutService extends BaseService<StoreSalesProducts> {

    private static final Logger log = LoggerFactory.getLogger(StockOutService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(StoreSalesProducts.class);
        Core.runTimeModelType.set(StoreSalesProductsModel.class);
    }

    public ResponseMessage saveStockOut(RequestMessage requestMessage) {
        ResponseMessage responseMessage;// = new ResponseMessage();
        StoreSalesProductsModel stockInModel;
        //byte[] imageByte;
        StoreSalesProductsModel searchDuplicateStoreSalesProductsModel;
        List<StoreSalesProductsModel> foundDuplicateStoreSalesProductsModelList;
        try {
            stockInModel = Core.processRequestMessage(requestMessage, StoreSalesProductsModel.class);
            // "name", "category_id", "model_no", "brand", "barcode"
            // search for duplicate product

            /*
            if (stockInModel != null && !ObjectUtils.isEmpty(stockInModel)) {
                searchDuplicateStockModel = new StoreSalesProductsModel();
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
                responseMessage.message = "StoreSalesProducts save successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to save StoreSalesProducts";
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

    public ResponseMessage updateStockOut(RequestMessage requestMessage) {
        ResponseMessage responseMessage=null;
        StoreSalesProductsModel requestedStoreSalesProductsModel,
                searchDuplicateStockModel,
                oldStoreSalesProductsModel,
                updatedStoreSalesProductsModel;
        List<StoreSalesProductsModel> foundDuplicateStoreSalesProductsModelList;
        int countPropertyValueDifference;
        int acceptedUpdatePropertyDifference=3;
        //byte[] imageByte;
        try {


            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(StoreSalesProductsModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/


            requestedStoreSalesProductsModel = Core.processRequestMessage(requestMessage, StoreSalesProductsModel.class);

            // retrieved old store to update created and created date.
            oldStoreSalesProductsModel = this.getByIdActiveStatus(requestedStoreSalesProductsModel.getId());


            // "name", "category_id", "model_no", "brand", "barcode"
            // search for duplicate product

            if (requestedStoreSalesProductsModel != null && !ObjectUtils.isEmpty(requestedStoreSalesProductsModel)) {
                searchDuplicateStockModel = new StoreSalesProductsModel();
//                searchDuplicateStockModel.setName(requestedStockModel.getName());
//                searchDuplicateStockModel.setCategoryId(requestedStockModel.getCategoryId());
//                searchDuplicateStockModel.setBrandId(requestedStockModel.getBrandId());
//                searchDuplicateStockModel.setModelNo(requestedStockModel.getModelNo());
//                searchDuplicateStockModel.setBarcode(requestedStockModel.getBarcode());
                foundDuplicateStoreSalesProductsModelList = this.getAllByConditionWithActive(searchDuplicateStockModel);

                if (foundDuplicateStoreSalesProductsModelList.size() == 0) {
                    updatedStoreSalesProductsModel = this.update(requestedStoreSalesProductsModel, oldStoreSalesProductsModel);
                    responseMessage = this.buildResponseMessage(updatedStoreSalesProductsModel);
                    responseMessage.httpStatus = HttpStatus.OK.value();
                    responseMessage.message = "Successfully StoreSalesProducts updated";
                    //this.commit();
                    return responseMessage;
                }

                if(foundDuplicateStoreSalesProductsModelList.size()>0){

                    countPropertyValueDifference = Core.comparePropertyValueDifference(requestedStoreSalesProductsModel, oldStoreSalesProductsModel);
                    if(countPropertyValueDifference==acceptedUpdatePropertyDifference
                            || countPropertyValueDifference<acceptedUpdatePropertyDifference){
                        updatedStoreSalesProductsModel = this.update(requestedStoreSalesProductsModel, oldStoreSalesProductsModel);
                        responseMessage = this.buildResponseMessage(updatedStoreSalesProductsModel);
                        responseMessage.httpStatus = HttpStatus.OK.value();
                        responseMessage.message = "Successfully StoreSalesProducts updated";
                        return responseMessage;
                    }else {
                        responseMessage = this.buildResponseMessage(requestedStoreSalesProductsModel);
                        responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                        responseMessage.message = "Failed to update StoreSalesProducts";
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


    public ResponseMessage deleteStockOut(UUID id) {
        ResponseMessage responseMessage;
        StoreSalesProductsModel stockInModel;
        Integer numberOfDeletedRow;
        try {
            //StoreSalesProductsModel = Core.processRequestMessage(requestMessage, StoreSalesProductsModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(StoreSalesProductsModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/
            stockInModel = this.getById(id);
            //StoreSalesProductsModel = this.softDelete(StoreSalesProductsModel);

            numberOfDeletedRow = this.deleteSoft(id);

            responseMessage = this.buildResponseMessage(numberOfDeletedRow);

            if (stockInModel != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "StoreSalesProducts deleted successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to deleted StoreSalesProducts";
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

    public ResponseMessage getByStockOutId(UUID id) {
        ResponseMessage responseMessage;
        StoreSalesProductsModel stockInModel;
        //String base64textString[];

        try {
            stockInModel = this.getByIdActiveStatus(id);
            //stockInModel.setImage(Base64.decodeBase64(stockInModel.getImage()));

            responseMessage = buildResponseMessage(stockInModel);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Get requested StoreSalesProducts successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to requested StoreSalesProducts";
            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            //this.rollBack();
            ex.printStackTrace();
            log.error("getByStockId -> got exception");
        }

        return responseMessage;
    }


    public ResponseMessage getAllStockOut(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        List<StoreSalesProductsModel> list;
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
                        .append("FROM StoreSalesProducts p ")
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

                list = this.executeHqlQuery(queryBuilderString.toString(), StoreSalesProductsModel.class, SqlEnum.QueryType.Join.get());

            } else {
                list = this.getAll();
            }



            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(StoreSalesProductsModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/


            responseMessage = this.buildResponseMessage(list);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Get all StoreSalesProducts successfully";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get StoreSalesProducts";
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
