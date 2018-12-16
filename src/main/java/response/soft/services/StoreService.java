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

    public ResponseMessage saveStore(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        StoreModel storeModel;
        try {
            storeModel = Core.processRequestMessage(requestMessage, StoreModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            storeModel = Core.getTrimmedModel(storeModel);
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
        ResponseMessage responseMessage;
        StoreModel storeModel;
        try {
            storeModel = Core.processRequestMessage(requestMessage, StoreModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            storeModel = this.update(storeModel);
            responseMessage = this.buildResponseMessage(storeModel);

            if (storeModel != null) {
                responseMessage.message = "Store update successfully!";
                //this.commit();
            } else {
                responseMessage.message = "Failed to update Store";
                //this.rollBack();
            }
            responseMessage.httpStatus = HttpStatus.OK.value();
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
            storeModel=this.getById(id);
            //categoryModel = this.softDelete(categoryModel);

            numberOfDeletedRow=this.deleteSoft(id);

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
            storeModel=this.getById(id);

            responseMessage = buildResponseMessage(storeModel);

            if (responseMessage.data != null) {
                responseMessage.message = "Get requested Store successfully";
            } else {
                responseMessage.message = "Failed to requested Store";
            }
            responseMessage.httpStatus = HttpStatus.OK.value();
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
        String searchKey=null;
        //StoreModel brandSearchModel;
        StringBuilder queryBuilderString;
        try {
            Core.processRequestMessage(requestMessage);

            dataTableRequest = requestMessage.dataTableRequest;
            if(dataTableRequest!=null) {
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
                queryBuilderString.append("SELECT v.id, ")
                        .append("v.name, ")
                        .append("v.phoneNo, ")
                        .append("v.email, ")
                        .append("v.address, ")
                        .append("v.description ")
                        .append("FROM Store v ")
                        .append("WHERE ")
                        .append("( ")
                        .append("lower(v.name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.phoneNo) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.email) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.address) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.description) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.description) LIKE '%" + searchKey + "%' ")
                        .append(") ")
                        .append("AND v.status="+SqlEnum.Status.Active.get());

                list = this.executeHqlQuery(queryBuilderString.toString(),StoreModel.class,SqlEnum.QueryType.Join.get());
                //============ full text search ===========================================
            }else {
                list = this.getAll();
            }

            responseMessage = this.buildResponseMessage(list);

            if (responseMessage.data != null) {
                responseMessage.message = "Get all vendor successfully";
                //this.commit();
            } else {
                responseMessage.message = "Failed to get vendor";
                //this.rollBack();
            }
            responseMessage.httpStatus = HttpStatus.OK.value();
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("getAllVendor -> save got exception");
        }
        return responseMessage;
    }
}
