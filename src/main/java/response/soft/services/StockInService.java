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
import response.soft.entities.StockIn;
import response.soft.model.StockInModel;

import java.util.List;
import java.util.UUID;

@Service
public class StockInService extends BaseService<StockIn> {

    private static final Logger log = LoggerFactory.getLogger(StockInService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(StockIn.class);
        Core.runTimeModelType.set(StockInModel.class);
    }

    public ResponseMessage saveStockIn(RequestMessage requestMessage) {
        ResponseMessage responseMessage;// = new ResponseMessage();
        StockInModel stockInModel;
        //byte[] imageByte;
        StockInModel searchDuplicateStockInModel;
        List<StockInModel> foundDuplicateStockInModelList;
        try {
            stockInModel = Core.processRequestMessage(requestMessage, StockInModel.class);
            // "name", "category_id", "model_no", "brand", "barcode"
            // search for duplicate product

            /*
            if (stockInModel != null && !ObjectUtils.isEmpty(stockInModel)) {
                searchDuplicateStockModel = new StockInModel();
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
                responseMessage.message = "StockIn save successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to save StockIn";
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

    public ResponseMessage updateStockIn(RequestMessage requestMessage) {
        ResponseMessage responseMessage=null;
        StockInModel requestedStockInModel,
                searchDuplicateStockModel,
                oldStockInModel,
                updatedStockInModel;
        List<StockInModel> foundDuplicateStockInModelList;
        int countPropertyValueDifference;
        int acceptedUpdatePropertyDifference=3;
        //byte[] imageByte;
        try {


            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(StockInModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/


            requestedStockInModel = Core.processRequestMessage(requestMessage, StockInModel.class);

            // retrieved old store to update created and created date.
            oldStockInModel = this.getByIdActiveStatus(requestedStockInModel.getId());


            // "name", "category_id", "model_no", "brand", "barcode"
            // search for duplicate product

            if (requestedStockInModel != null && !ObjectUtils.isEmpty(requestedStockInModel)) {
                searchDuplicateStockModel = new StockInModel();
//                searchDuplicateStockModel.setName(requestedStockModel.getName());
//                searchDuplicateStockModel.setCategoryId(requestedStockModel.getCategoryId());
//                searchDuplicateStockModel.setBrandId(requestedStockModel.getBrandId());
//                searchDuplicateStockModel.setModelNo(requestedStockModel.getModelNo());
//                searchDuplicateStockModel.setBarcode(requestedStockModel.getBarcode());
                foundDuplicateStockInModelList = this.getAllByConditionWithActive(searchDuplicateStockModel);

                if (foundDuplicateStockInModelList.size() == 0) {
                    updatedStockInModel = this.update(requestedStockInModel,oldStockInModel);
                    responseMessage = this.buildResponseMessage(updatedStockInModel);
                    responseMessage.httpStatus = HttpStatus.OK.value();
                    responseMessage.message = "Successfully StockIn updated";
                    //this.commit();
                    return responseMessage;
                }

                if(foundDuplicateStockInModelList.size()>0){

                    countPropertyValueDifference = Core.comparePropertyValueDifference(requestedStockInModel,oldStockInModel);
                    if(countPropertyValueDifference==acceptedUpdatePropertyDifference
                            || countPropertyValueDifference<acceptedUpdatePropertyDifference){
                        updatedStockInModel = this.update(requestedStockInModel,oldStockInModel);
                        responseMessage = this.buildResponseMessage(updatedStockInModel);
                        responseMessage.httpStatus = HttpStatus.OK.value();
                        responseMessage.message = "Successfully StockIn updated";
                        return responseMessage;
                    }else {
                        responseMessage = this.buildResponseMessage(requestedStockInModel);
                        responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                        responseMessage.message = "Failed to update StockIn";
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


    public ResponseMessage deleteStockIn(UUID id) {
        ResponseMessage responseMessage;
        StockInModel stockInModel;
        Integer numberOfDeletedRow;
        try {
            //StockInModel = Core.processRequestMessage(requestMessage, StockInModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(StockInModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/
            stockInModel = this.getById(id);
            //StockInModel = this.softDelete(StockInModel);

            numberOfDeletedRow = this.deleteSoft(id);

            responseMessage = this.buildResponseMessage(numberOfDeletedRow);

            if (stockInModel != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "StockIn deleted successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to deleted StockIn";
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

    public ResponseMessage getByStockInId(UUID id) {
        ResponseMessage responseMessage;
        StockInModel stockInModel;
        //String base64textString[];

        try {
            stockInModel = this.getByIdActiveStatus(id);
            //stockInModel.setImage(Base64.decodeBase64(stockInModel.getImage()));

            responseMessage = buildResponseMessage(stockInModel);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Get requested StockIn successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to requested StockIn";
            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            //this.rollBack();
            ex.printStackTrace();
            log.error("getByStockId -> got exception");
        }

        return responseMessage;
    }


    public ResponseMessage getAllStockIn(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        List<StockInModel> list;
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
                        .append("FROM StockIn p ")
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

                list = this.executeHqlQuery(queryBuilderString.toString(), StockInModel.class, SqlEnum.QueryType.Join.get());

            } else {
                list = this.getAll();
            }



            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(StockInModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/


            responseMessage = this.buildResponseMessage(list);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Get all StockIn successfully";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get StockIn";
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
