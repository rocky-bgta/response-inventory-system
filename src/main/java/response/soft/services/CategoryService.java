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
import response.soft.entities.Category;
import response.soft.model.CategoryModel;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService extends BaseService<Category> {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(Category.class);
        Core.runTimeModelType.set(CategoryModel.class);
    }

    private ResponseMessage checkDuplicateCategory(CategoryModel categoryModel) throws Exception {
        ResponseMessage responseMessage;
        CategoryModel searchDuplicateCategoryModel;
        List<CategoryModel> foundDuplicateCategory;
        searchDuplicateCategoryModel = new CategoryModel();
        searchDuplicateCategoryModel.setName(categoryModel.getName());
        foundDuplicateCategory = this.getAllByConditionWithActive(searchDuplicateCategoryModel);
        if (foundDuplicateCategory.size() != 0) {
            responseMessage = this.buildResponseMessage();
            responseMessage.httpStatus = HttpStatus.CONFLICT.value();
            responseMessage.message = "Same Category Name already exist";
            return responseMessage;
        } else {
            return null;
        }
    }

    public ResponseMessage saveCategory(RequestMessage requestMessage) {
        ResponseMessage responseMessage;// = new ResponseMessage();
        CategoryModel categoryModel;
        try {
            categoryModel = Core.processRequestMessage(requestMessage, CategoryModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            // search for duplicate product
            if (categoryModel != null && !ObjectUtils.isEmpty(categoryModel)) {
                responseMessage = this.checkDuplicateCategory(categoryModel);
                if (responseMessage != null)
                    return responseMessage;
            }

            categoryModel = this.save(categoryModel);
            responseMessage = this.buildResponseMessage(categoryModel);

            if (categoryModel != null) {
                responseMessage.httpStatus = HttpStatus.CREATED.value();
                responseMessage.message = "Category save successfully!";
                Core.commitTransaction();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to save category";
                Core.rollBackTransaction();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            Core.rollBackTransaction();
            log.error("saveCategory -> save got exception");
        }
        return responseMessage;
    }

    public ResponseMessage updateCategory(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        CategoryModel categoryModel, categorySearchCondition,oldCategory;
        List<CategoryModel> categoryModelList;
        try {
            categoryModel = Core.processRequestMessage(requestMessage, CategoryModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            responseMessage = this.buildResponseMessage(categoryModel);


            // retrieved old vendor to update created and created date.
            oldCategory = this.getByIdActiveStatus(categoryModel.getId());

            categorySearchCondition = new CategoryModel();
            categorySearchCondition.setName(categoryModel.getName());
            categoryModelList = this.getAllByConditionWithActive(categorySearchCondition);
            if (categoryModelList.size() == 0) {
                categoryModel = this.update(categoryModel,oldCategory);
                if (categoryModel != null) {
                    responseMessage.message = "Category update successfully!";
                    responseMessage.httpStatus = HttpStatus.OK.value();
                    Core.commitTransaction();
                    return responseMessage;
                    //this.commit();
                }
            }


            if(categoryModelList.size()>0){
                if(StringUtils.equals(categoryModelList.get(0).getName(), categoryModel.getName())){
                    oldCategory = categoryModelList.get(0);
                    categoryModel = this.update(categoryModel,oldCategory);
                    if (categoryModel != null) {
                        responseMessage.message = "Category update successfully!";
                        responseMessage.httpStatus = HttpStatus.OK.value();
                        Core.commitTransaction();
                        //this.commit();
                    }
                }else {
                    responseMessage.httpStatus = HttpStatus.CONFLICT.value();
                    responseMessage.message = "Same Category Name already exist";
                    Core.rollBackTransaction();
                    //this.rollBack();
                }
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            Core.rollBackTransaction();
            log.error("updateCategory -> got exception");
        }
        return responseMessage;
    }

    public ResponseMessage deleteCategory(UUID id) {
        ResponseMessage responseMessage;
        CategoryModel categoryModel;
        Integer numberOfDeletedRow;
        try {
            //categoryModel = Core.processRequestMessage(requestMessage, CategoryModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/
            categoryModel=this.getById(id);
            //categoryModel = this.softDelete(categoryModel);

            numberOfDeletedRow=this.deleteSoft(id);

            responseMessage = this.buildResponseMessage(numberOfDeletedRow);

            if (numberOfDeletedRow != null && numberOfDeletedRow>0) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Category deleted successfully!";
                Core.commitTransaction();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to deleted category";
                Core.rollBackTransaction();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            Core.rollBackTransaction();
            log.error("deleteCategory -> got exception");
        }
        return responseMessage;
    }

    public ResponseMessage getByCategoryId(UUID id) {
        ResponseMessage responseMessage;
        CategoryModel categoryModel;

        try {
            categoryModel=this.getByIdActiveStatus(id);

            responseMessage = buildResponseMessage(categoryModel);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Get requested category successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to requested category";
            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            log.error("getByCategoryId -> got exception");
        }

        return responseMessage;
    }

    public ResponseMessage getByCategoryByStoreId(UUID storeId) {
        ResponseMessage responseMessage;
        List<CategoryModel> list;
        StringBuilder queryBuilderString;

        try {

            queryBuilderString = new StringBuilder();
            queryBuilderString
                    .append("SELECT DISTINCT c.id, ")
                    .append("c.name ")
                    .append("FROM Stock s ")
                    .append("INNER JOIN Product p ON s.productId = p.id ")
                    .append("INNER JOIN Category c ON p.categoryId = c.id ")
                    .append("INNER JOIN SalesAvailableStockView sasv on sasv.productId = p.id ")
                    .append("WHERE sasv.availableQty>0 AND ")
                    .append("s.storeId = '" + storeId+"'");


            list=this.executeHqlQuery(queryBuilderString.toString(),CategoryModel.class,SqlEnum.QueryType.Join.get());

            responseMessage = buildResponseMessage(list);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get requested category successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to requested category";
            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            log.error("getByCategoryByStoreId -> got exception");
        }

        return responseMessage;
    }

    public ResponseMessage getAllCategory(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        List<CategoryModel> list;

        //DataTableRequest dataTableRequest;
        String searchKey;
        //BrandModel brandSearchModel;
        StringBuilder queryBuilderString;

        try {
            //this.resetPaginationVariable();
            Core.processRequestMessage(requestMessage);
            searchKey = Core.dataTableSearchKey.get();

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/


            //dataTableRequest = requestMessage.dataTableRequest;
            if(searchKey!=null) {
                searchKey = searchKey.trim().toLowerCase();
            }

            //============ full text search ===========================================

            if (searchKey != null && !StringUtils.isEmpty(searchKey)) {

                queryBuilderString = new StringBuilder();
                queryBuilderString.append("SELECT c.id, ")
                        .append("c.name, ")
                        .append("c.description ")
                        .append("FROM Category c ")
                        .append("WHERE ")
                        .append("lower(c.name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(c.description) LIKE '%" + searchKey + "%' ")
                        .append("AND c.status="+SqlEnum.Status.Active.get());

                list = this.executeHqlQuery(queryBuilderString.toString(),CategoryModel.class,SqlEnum.QueryType.Join.get());
                //============ full text search ===========================================
            }else {
                list = this.getAll();
            }

            responseMessage = this.buildResponseMessage(list);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Get all category successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get category";
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            log.error("getAllCategory -> save got exception");
        }
        return responseMessage;
    }
}
