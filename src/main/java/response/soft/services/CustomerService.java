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

    private ResponseMessage checkDuplicateStore(CustomerModel vendorModel) throws Exception {
        ResponseMessage responseMessage;
        CustomerModel searchDuplicateCustomerModel;
        List<CustomerModel> foundDuplicateCustomer;
        searchDuplicateCustomerModel = new CustomerModel();
        searchDuplicateCustomerModel.setName(vendorModel.getName());
        foundDuplicateCustomer = this.getAllByConditionWithActive(searchDuplicateCustomerModel);
        if (foundDuplicateCustomer.size() != 0) {
            responseMessage = this.buildResponseMessage();
            responseMessage.httpStatus = HttpStatus.CONFLICT.value();
            responseMessage.message = "Same Customer name already exist";
            return responseMessage;
        } else {
            return null;
        }
    }

    public ResponseMessage saveCustomer(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        CustomerModel vendorModel;
        try {
            vendorModel = Core.processRequestMessage(requestMessage, CustomerModel.class);

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
        CustomerModel vendorModel, vendorSearchCondition,oldCustomer;
        List<CustomerModel> vendorModelList;
        try {
            vendorModel = Core.processRequestMessage(requestMessage, CustomerModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            responseMessage = this.buildResponseMessage(vendorModel);

            // retrieved old vendor to update created and created date.
            oldCustomer = this.getByIdActiveStatus(vendorModel.getId());



            vendorSearchCondition = new CustomerModel();
            vendorSearchCondition.setName(vendorModel.getName());
            vendorModelList = this.getAllByConditionWithActive(vendorSearchCondition);
            if (vendorModelList.size() == 0) {
                vendorModel = this.update(vendorModel,oldCustomer);
                if (vendorModel != null) {
                    responseMessage.message = "Customer update successfully!";
                    responseMessage.httpStatus = HttpStatus.OK.value();
                    return responseMessage;
                    //this.commit();
                }
            }


            if(vendorModelList.size()>0){
                if(StringUtils.equals(vendorModelList.get(0).getName(), vendorModel.getName())){
                    oldCustomer = vendorModelList.get(0);
                    vendorModel = this.update(vendorModel,oldCustomer);
                    if (vendorModel != null) {
                        responseMessage.message = "Customer update successfully!";
                        responseMessage.httpStatus = HttpStatus.OK.value();
                        //this.commit();
                    }
                }else {
                    responseMessage.httpStatus = HttpStatus.CONFLICT.value();
                    responseMessage.message = "Same Customer name already exist";
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
        String searchKey=null;
        //CustomerModel brandSearchModel;
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
                        .append("FROM Customer v ")
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
