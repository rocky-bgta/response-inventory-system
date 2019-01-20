package response.soft.services;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import response.soft.appenum.SqlEnum;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.core.datatable.model.DataTableRequest;
import response.soft.entities.Stock;
import response.soft.entities.view.AvailableStockView;
import response.soft.model.StockModel;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.UUID;

@Service
public class StockService extends BaseService<Stock> {

    private static final Logger log = LoggerFactory.getLogger(StockService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(Stock.class);
        Core.runTimeModelType.set(StockModel.class);
    }

    public ResponseMessage saveStock(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        StockModel stockModel;
        try {
            stockModel = Core.processRequestMessage(requestMessage, StockModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            // search for duplicate product

            /*if (vendorModel != null && !ObjectUtils.isEmpty(vendorModel)) {
                responseMessage = this.checkDuplicateStore(vendorModel);
                if (responseMessage != null)
                    return responseMessage;
            }*/


            stockModel = this.save(stockModel);
            responseMessage = this.buildResponseMessage(stockModel);

            if (stockModel != null) {
                responseMessage.httpStatus = HttpStatus.CREATED.value();
                responseMessage.message = "Stock save successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to save Stock";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage("Internal server error");
            ex.printStackTrace();
            //this.rollBack();
            log.error("saveStock -> save got exception");
        }
        return responseMessage;
    }

    public ResponseMessage updateStock(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        StockModel requestedStockModel, vendorSearchCondition,oldStockModel;
        List<StockModel> vendorModelList;
        try {
            requestedStockModel = Core.processRequestMessage(requestMessage, StockModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            responseMessage = this.buildResponseMessage(requestedStockModel);

            // retrieved old vendor to update created and created date.
            oldStockModel = this.getByIdActiveStatus(requestedStockModel.getId());



            vendorSearchCondition = new StockModel();
            //vendorSearchCondition.setName(vendorModel.getName());
            vendorModelList = this.getAllByConditionWithActive(vendorSearchCondition);
            if (vendorModelList.size() == 0) {
                requestedStockModel = this.update(requestedStockModel,oldStockModel);
                if (requestedStockModel != null) {
                    responseMessage.message = "Stock update successfully!";
                    responseMessage.httpStatus = HttpStatus.OK.value();
                    return responseMessage;
                    //this.commit();
                }
            }


           /* if(vendorModelList.size()>0){
                if(StringUtils.equals(vendorModelList.get(0).getName(), vendorModel.getName())){
                    oldStock = vendorModelList.get(0);
                    vendorModel = this.update(vendorModel,oldStock);
                    if (vendorModel != null) {
                        responseMessage.message = "Stock update successfully!";
                        responseMessage.httpStatus = HttpStatus.OK.value();
                        //this.commit();
                    }
                }else {
                    responseMessage.httpStatus = HttpStatus.CONFLICT.value();
                    responseMessage.message = "Same Stock name already exist";
                    //this.rollBack();
                }
            }*/


        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("updateStock -> got exception");
        }
        return responseMessage;
    }


    public ResponseMessage deleteStock(UUID id) {
        ResponseMessage responseMessage;
        StockModel vendorModel;
        Integer numberOfDeletedRow;
        try {
            //categoryModel = Core.processRequestMessage(requestMessage, StockModel.class);

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
                responseMessage.message = "Stock deleted successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to deleted Stock";
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

    public ResponseMessage getByStockId(UUID id) {
        ResponseMessage responseMessage;
        StockModel vendorModel;

        try {
            vendorModel=this.getByIdActiveStatus(id);

            responseMessage = buildResponseMessage(vendorModel);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Get requested Stock successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to requested Stock";
            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            //this.rollBack();
            ex.printStackTrace();
            log.error("getByStockId -> got exception");
        }

        return responseMessage;
    }


    public ResponseMessage getAllStock(RequestMessage requestMessage, UUID storeId, UUID productId) {
        ResponseMessage responseMessage;
        List<AvailableStockView> list;
        DataTableRequest dataTableRequest;
        String searchKey=null;
        StringBuilder queryBuilderString =new StringBuilder();
        try {
            this.resetPaginationVariable();
            Core.processRequestMessage(requestMessage);

            dataTableRequest = requestMessage.dataTableRequest;
            if(dataTableRequest!=null && !StringUtils.equals(dataTableRequest.search.value,"string")) {
                searchKey = dataTableRequest.search.value;
                searchKey = searchKey.trim().toLowerCase();
            }

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/



            //============ full text search ===========================================

            if ((dataTableRequest != null && !StringUtils.isEmpty(searchKey))|| storeId!=null || productId!=null) {

               /* queryBuilderString.append("SELECT v.id, ")
                        .append("v.name, ")
                        .append("v.phoneNo, ")
                        .append("v.email, ")
                        .append("v.address, ")
                        .append("v.description ")
                        .append("FROM Stock v ")
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

                */
                //Boolean isWhereAdded=false;
                queryBuilderString.append("SELECT v FROM AvailableStockView v WHERE v.availableQty>0 ");
                if(storeId!=null &&  storeId instanceof UUID){
                    queryBuilderString.append("AND v.storeId ='"+storeId+"' ");
                    //isWhereAdded=true;
                }
                if(productId!=null && storeId instanceof UUID){
                    //if(isWhereAdded){
                        queryBuilderString.append("AND v.productId='"+productId+"'");
                    //}else {
                      //  queryBuilderString.append("WHERE v.productId='"+productId+"'");
                   // }
                }



                list = this.executeHqlQuery(queryBuilderString.toString(),AvailableStockView.class,SqlEnum.QueryType.View.get());
                //============ full text search ===========================================
            }else {
                queryBuilderString.setLength(0);
                queryBuilderString.append("SELECT v FROM AvailableStockView v WHERE v.availableQty>0");

                list = this.executeHqlQuery(queryBuilderString.toString(),AvailableStockView.class,SqlEnum.QueryType.View.get());
            }

            responseMessage = this.buildResponseMessage(list);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get all Stock successfully";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get Stock";
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
