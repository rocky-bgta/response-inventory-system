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
import response.soft.entities.Store;
import response.soft.model.StoreModel;

import java.util.List;
import java.util.UUID;

@Service
public class StoreService extends BaseService<Store> {

    private static final Logger log = LoggerFactory.getLogger(StoreService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(Store.class);
        Core.runTimeModelType.set(StoreModel.class);
    }


    private ResponseMessage checkDuplicateStore(StoreModel storeModel) throws Exception {
        ResponseMessage responseMessage;
        StoreModel searchDuplicateStoreModel;
        List<StoreModel> foundDuplicateStore;
        searchDuplicateStoreModel = new StoreModel();
        searchDuplicateStoreModel.setName(storeModel.getName());
        foundDuplicateStore = this.getAllByConditionWithActive(searchDuplicateStoreModel);
        if (foundDuplicateStore.size() != 0) {
            responseMessage = this.buildResponseMessage();
            responseMessage.httpStatus = HttpStatus.CONFLICT.value();
            responseMessage.message = "Same Store name already exist";
            return responseMessage;
        } else {
            return null;
        }
    }

    public ResponseMessage saveStore(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        StoreModel storeModel;

        try {
            storeModel = Core.processRequestMessage(requestMessage, StoreModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/


            // search for duplicate product
            if (storeModel != null && !ObjectUtils.isEmpty(storeModel)) {
                responseMessage = this.checkDuplicateStore(storeModel);
                if (responseMessage != null)
                    return responseMessage;
            }

            storeModel = this.save(storeModel);
            responseMessage = this.buildResponseMessage(storeModel);

            if (storeModel != null) {
                responseMessage.httpStatus = HttpStatus.CREATED.value();
                responseMessage.message = "Store save successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to save Store";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage("Internal server error");
            ex.printStackTrace();
            //this.rollBack();
            log.error("saveVendor -> save got exception");
        }
        return responseMessage;
    }

    public ResponseMessage updateStore(RequestMessage requestMessage) {
        ResponseMessage responseMessage=null;
        StoreModel storeModel, storeSearchCondition,oldStore;
        List<StoreModel> storeModelList;
        try {
            storeModel = Core.processRequestMessage(requestMessage, StoreModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/



            // retrieved old store to update created and created date.
            oldStore = this.getByIdActiveStatus(storeModel.getId());


            storeSearchCondition = new StoreModel();
            storeSearchCondition.setName(storeModel.getName());
            storeModelList = this.getAllByConditionWithActive(storeSearchCondition);
            if (storeModelList.size() == 0) {
                storeModel = this.update(storeModel,oldStore);
                if (storeModel != null) {
                    responseMessage = this.buildResponseMessage(storeModel);
                    responseMessage.message = "Store update successfully!";
                    responseMessage.httpStatus = HttpStatus.OK.value();
                    return responseMessage;
                    //this.commit();
                }
            }

            if(storeModelList.size()>0){
                if(StringUtils.equals(storeModelList.get(0).getName(),storeModel.getName())){
                    oldStore = storeModelList.get(0);
                    storeModel = this.update(storeModel,oldStore);
                    if (storeModel != null) {
                        responseMessage = this.buildResponseMessage(storeModel);
                        responseMessage.message = "Store update successfully!";
                        responseMessage.httpStatus = HttpStatus.OK.value();
                        return responseMessage;
                        //this.commit();
                    }
                }else {
                    responseMessage = this.buildResponseMessage(storeModel);
                    responseMessage.httpStatus = HttpStatus.CONFLICT.value();
                    responseMessage.message = "Same Store name already exist";
                    //this.rollBack();
                }
            }else {
                responseMessage = this.buildFailedResponseMessage("Failed to Update Store");
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("updateVendor -> got exception");
        }
        return responseMessage;
    }


    public ResponseMessage deleteStore(UUID id) {
        ResponseMessage responseMessage;
        StoreModel storeModel;
        Integer numberOfDeletedRow;
        try {
            //categoryModel = Core.processRequestMessage(requestMessage, StoreModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/
            storeModel = this.getById(id);
            //categoryModel = this.softDelete(categoryModel);

            numberOfDeletedRow = this.deleteSoft(id);

            responseMessage = this.buildResponseMessage(numberOfDeletedRow);

            if (storeModel != null) {
                responseMessage.message = "Store deleted successfully!";
                //this.commit();
            } else {
                responseMessage.message = "Failed to deleted vendor";
                //this.rollBack();
            }
            responseMessage.httpStatus = HttpStatus.OK.value();
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("deleteVendor -> got exception");
        }
        return responseMessage;
    }

    public ResponseMessage getByStoreId(UUID id) {
        ResponseMessage responseMessage;
        StoreModel storeModel;

        try {
            storeModel = this.getByIdActiveStatus(id);

            responseMessage = buildResponseMessage(storeModel);

            if (responseMessage.data != null) {
                responseMessage.message = "Get requested Store successfully";
                responseMessage.httpStatus = HttpStatus.OK.value();
            } else {
                responseMessage.message = "Failed to requested Store";
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            //this.rollBack();
            ex.printStackTrace();
            log.error("getByVendorId -> got exception");
        }

        return responseMessage;
    }


    public ResponseMessage getAllStore(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        List<StoreModel> list;
        DataTableRequest dataTableRequest;
        String searchKey = null;
        //StoreModel brandSearchModel;
        StringBuilder queryBuilderString;
        try {
            Core.processRequestMessage(requestMessage);

            dataTableRequest = requestMessage.dataTableRequest;
            if (dataTableRequest != null && dataTableRequest.length!=null) {
                searchKey = dataTableRequest.search.value;
                searchKey = searchKey.trim().toLowerCase();
            }

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/


            //============ full text search ===========================================

            if (dataTableRequest != null && !StringUtils.isEmpty(searchKey)) {

                queryBuilderString = new StringBuilder();
                queryBuilderString.append("SELECT s.id, ")
                        .append("s.name, ")
                        .append("s.owner, ")
                        .append("s.phoneNo, ")
                        .append("s.address, ")
                        .append("s.email, ")
                        .append("s.comment ")
                        .append("FROM Store s ")
                        .append("WHERE ")
                        .append("( ")
                        .append("lower(s.name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(s.owner) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(s.phoneNo) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(s.address) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(s.email) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(s.comment) LIKE '%" + searchKey + "%' ")
                        .append(") ")
                        .append("AND s.status=" + SqlEnum.Status.Active.get());

                list = this.executeHqlQuery(queryBuilderString.toString(), StoreModel.class, SqlEnum.QueryType.Join.get());
                //============ full text search ===========================================
            } else {
                list = this.getAll();
            }

            responseMessage = this.buildResponseMessage(list);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get all Store successfully";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get Store";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("getAllVendor -> getAllVendor got exception");
        }
        return responseMessage;
    }
}
