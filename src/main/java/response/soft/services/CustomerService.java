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
import response.soft.entities.Customer;
import response.soft.model.CustomerModel;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService extends BaseService<Customer> {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(Customer.class);
        Core.runTimeModelType.set(CustomerModel.class);
    }

    private Boolean checkDuplicateModelFound(CustomerModel requestedCustomerModel) throws Exception {
        Boolean isDuplicate;
        try {
            CustomerModel customerSearchCondition;
            List<CustomerModel> foundDuplicateCustomer;
            customerSearchCondition = new CustomerModel();
            customerSearchCondition.setName(requestedCustomerModel.getName());
            customerSearchCondition.setPhoneNo1(requestedCustomerModel.getPhoneNo1());
            customerSearchCondition.setAddress(requestedCustomerModel.getAddress());
            foundDuplicateCustomer = this.getAllByConditionWithActive(customerSearchCondition);
            if (foundDuplicateCustomer.size() != 0) {
                isDuplicate = true;
            } else {
                isDuplicate = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return isDuplicate;
    }

    public ResponseMessage saveCustomer(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        CustomerModel requestCustomerModel, saveCustomerModel;
        Boolean checkDuplicateModel;
        try {
            requestCustomerModel = Core.processRequestMessage(requestMessage, CustomerModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            // search for duplicate product

            if (requestCustomerModel != null && !ObjectUtils.isEmpty(requestCustomerModel)) {
                checkDuplicateModel = this.checkDuplicateModelFound(requestCustomerModel);
                if(checkDuplicateModel){
                    responseMessage = this.buildResponseMessage();
                    responseMessage.httpStatus = HttpStatus.CONFLICT.value();
                    responseMessage.message = "Same Customer Name ,Phone No and Address already exist";
                    return responseMessage;
                }
            }

            //requestCustomerModel.setCustomerCode(UUID.randomUUID().toString());

            saveCustomerModel = this.save(requestCustomerModel);
            responseMessage = this.buildResponseMessage(saveCustomerModel);

            if (saveCustomerModel != null) {
                responseMessage.httpStatus = HttpStatus.CREATED.value();
                responseMessage.message = "Customer save successfully!";
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
            log.error("saveCustomer -> save got exception");
        }
        return responseMessage;
    }

    public ResponseMessage updateCustomer(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        CustomerModel requestedCustomerModel,oldCustomerModel;
        //List<CustomerModel> customerModelList;
        Boolean checkDuplicateModel;
        int allowChangePropertyValueForCustomer=4, actualPropertyChangeValueChangeInRequest;
        try {
            requestedCustomerModel = Core.processRequestMessage(requestMessage, CustomerModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            responseMessage = this.buildResponseMessage(requestedCustomerModel);

            // retrieved old vendor to update created and created date.
            oldCustomerModel = this.getByIdActiveStatus(requestedCustomerModel.getId());

            checkDuplicateModel = this.checkDuplicateModelFound(requestedCustomerModel);

/*
            customerSearchCondition = new CustomerModel();
            customerSearchCondition.setName(requestedCustomerModel.getName());
            customerSearchCondition.setPhoneNo1(requestedCustomerModel.getPhoneNo1());
            customerSearchCondition.setAddress(requestedCustomerModel.getAddress());
            customerModelList = this.getAllByConditionWithActive(customerSearchCondition);
            */

            if (checkDuplicateModel) {
                requestedCustomerModel = this.update(requestedCustomerModel,oldCustomerModel);
                if (requestedCustomerModel != null) {
                    responseMessage.message = "Customer update successfully!";
                    responseMessage.httpStatus = HttpStatus.OK.value();
                    return responseMessage;
                    //this.commit();
                }
            }else {

                actualPropertyChangeValueChangeInRequest=
                        Core.comparePropertyValueDifference(requestedCustomerModel,oldCustomerModel);

                if((allowChangePropertyValueForCustomer>=actualPropertyChangeValueChangeInRequest)){
                    requestedCustomerModel = this.update(requestedCustomerModel,oldCustomerModel);
                    if (requestedCustomerModel != null) {
                        responseMessage.message = "Customer update successfully!";
                        responseMessage.httpStatus = HttpStatus.OK.value();
                        //this.commit();
                    }
                }else {
                    responseMessage.httpStatus = HttpStatus.CONFLICT.value();
                    responseMessage.message = "Failed to update Customer information!!!";
                    //this.rollBack();
                }
            }


        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("updateCustomer -> got exception");
        }
        return responseMessage;
    }


    public ResponseMessage deleteCustomer(UUID id) {
        ResponseMessage responseMessage;
        CustomerModel vendorModel;
        Integer numberOfDeletedRow;
        try {
            //categoryModel = Core.processRequestMessage(requestMessage, CustomerModel.class);

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
                responseMessage.message = "Customer deleted successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to deleted vendor";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("deleteCustomer -> got exception");
        }
        return responseMessage;
    }

    public ResponseMessage getByCustomerId(UUID id) {
        ResponseMessage responseMessage;
        CustomerModel vendorModel;

        try {
            vendorModel=this.getByIdActiveStatus(id);

            responseMessage = buildResponseMessage(vendorModel);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Get requested vendor successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to requested vendor";
            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            //this.rollBack();
            ex.printStackTrace();
            log.error("getByCustomerId -> got exception");
        }

        return responseMessage;
    }


    public ResponseMessage getAllCustomer(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        List<CustomerModel> list;
        DataTableRequest dataTableRequest;
        CustomerModel requestedCustomerModel;
        String searchKey=null;
        //CustomerModel brandSearchModel;
        StringBuilder queryBuilderString;
        try {
            requestedCustomerModel = Core.processRequestMessage(requestMessage);

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
                queryBuilderString.append("SELECT e.id, ")
                        .append("e.name, ")
                        .append("e.phoneNo1, ")
                        .append("e.phoneNo2, ")
                        .append("e.address, ")
                        .append("e.email, ")
                        .append("CAST(e.activity AS string), ")
                        .append("e.comment ")
                        .append("FROM Customer e ")
                        .append("WHERE ")
                        .append("( ")
                        .append("lower(e.name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(e.phoneNo1) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(e.phoneNo2) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(e.email) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(e.address) LIKE '%" + searchKey + "%' ")
                        .append("OR CAST(e.activity AS string) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(e.comment) LIKE '%" + searchKey + "%' ")
                        .append(") ")
                        .append("AND e.status="+SqlEnum.Status.Active.get());

                list = this.executeHqlQuery(queryBuilderString.toString(),CustomerModel.class,SqlEnum.QueryType.Join.get());
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
            log.error("getAllCustomer -> save got exception");
        }
        return responseMessage;
    }
}
