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
import response.soft.entities.Vendor;
import response.soft.model.VendorModel;

import java.util.List;
import java.util.UUID;

@Service
public class VendorService extends BaseService<Vendor> {

    private static final Logger log = LoggerFactory.getLogger(VendorService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(Vendor.class);
        Core.runTimeModelType.set(VendorModel.class);
    }

    private ResponseMessage checkDuplicateStore(VendorModel vendorModel) throws Exception {
        ResponseMessage responseMessage;
        VendorModel searchDuplicateVendorModel;
        List<VendorModel> foundDuplicateVendor;
        searchDuplicateVendorModel = new VendorModel();
        searchDuplicateVendorModel.setName(vendorModel.getName());
        foundDuplicateVendor = this.getAllByConditionWithActive(searchDuplicateVendorModel);
        if (foundDuplicateVendor.size() != 0) {
            responseMessage = this.buildResponseMessage();
            responseMessage.httpStatus = HttpStatus.CONFLICT.value();
            responseMessage.message = "Same Vendor name already exist";
            return responseMessage;
        } else {
            return null;
        }
    }

    public ResponseMessage saveVendor(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        VendorModel vendorModel;
        try {
            vendorModel = Core.processRequestMessage(requestMessage, VendorModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            // search for duplicate product
            if (vendorModel != null && !ObjectUtils.isEmpty(vendorModel)) {
                responseMessage = this.checkDuplicateStore(vendorModel);
                if (responseMessage != null)
                    return responseMessage;
            }


            vendorModel = this.save(vendorModel);
            responseMessage = this.buildResponseMessage(vendorModel);

            if (vendorModel != null) {
                responseMessage.httpStatus = HttpStatus.CREATED.value();
                responseMessage.message = "Vendor save successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to save vendor";
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

    public ResponseMessage updateVendor(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        VendorModel vendorModel, vendorSearchCondition,oldVendor;
        List<VendorModel> vendorModelList;
        try {
            vendorModel = Core.processRequestMessage(requestMessage, VendorModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            responseMessage = this.buildResponseMessage(vendorModel);

            // retrieved old vendor to update created and created date.
            oldVendor = this.getByIdActiveStatus(vendorModel.getId());



            vendorSearchCondition = new VendorModel();
            vendorSearchCondition.setName(vendorModel.getName());
            vendorModelList = this.getAllByConditionWithActive(vendorSearchCondition);
            if (vendorModelList.size() == 0) {
                vendorModel = this.update(vendorModel,oldVendor);
                if (vendorModel != null) {
                    responseMessage.message = "Vendor update successfully!";
                    responseMessage.httpStatus = HttpStatus.OK.value();
                    return responseMessage;
                    //this.commit();
                }
            }


            if(vendorModelList.size()>0){
                if(StringUtils.equals(vendorModelList.get(0).getName(), vendorModel.getName())){
                    //oldVendor = vendorModelList.get(0);
                    vendorModel = this.update(vendorModel,oldVendor);
                    if (vendorModel != null) {
                        responseMessage.message = "Vendor update successfully!";
                        responseMessage.httpStatus = HttpStatus.OK.value();
                        //this.commit();
                    }
                }else {
                    responseMessage.httpStatus = HttpStatus.CONFLICT.value();
                    responseMessage.message = "Same Vendor name already exist";
                    //this.rollBack();
                }
            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("updateVendor -> got exception");
        }
        return responseMessage;
    }


    public ResponseMessage deleteVendor(UUID id) {
        ResponseMessage responseMessage;
        VendorModel vendorModel;
        Integer numberOfDeletedRow;
        try {
            //categoryModel = Core.processRequestMessage(requestMessage, VendorModel.class);

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
                responseMessage.message = "Vendor deleted successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to deleted Vendor";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("deleteVendor -> got exception");
        }
        return responseMessage;
    }

    public ResponseMessage getByVendorId(UUID id) {
        ResponseMessage responseMessage;
        VendorModel vendorModel;

        try {
            vendorModel=this.getByIdActiveStatus(id);

            responseMessage = buildResponseMessage(vendorModel);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Get requested Vendor successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to requested Vendor";
            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            //this.rollBack();
            ex.printStackTrace();
            log.error("getByVendorId -> got exception");
        }

        return responseMessage;
    }


    public ResponseMessage getAllVendor(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        List<VendorModel> list;
        DataTableRequest dataTableRequest;
        String searchKey=null;
        //VendorModel brandSearchModel;
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
                        .append("FROM Vendor v ")
                        .append("WHERE ")
                        .append("( ")
                        .append("lower(v.name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.phoneNo) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.email) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.address) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.description) LIKE '%" + searchKey + "%' ")
                        .append(") ")
                        .append("AND v.status="+SqlEnum.Status.Active.get());

                list = this.executeHqlQuery(queryBuilderString.toString(),VendorModel.class,SqlEnum.QueryType.Join.get());
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
