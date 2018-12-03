package response.soft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import response.soft.constant.HttpConstant;
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

    public ResponseMessage saveCategory(RequestMessage requestMessage) {
        ResponseMessage responseMessage;// = new ResponseMessage();
        CategoryModel categoryModel;
        try {
            categoryModel = Core.processRequestMessage(requestMessage, CategoryModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            categoryModel = this.save(categoryModel);
            responseMessage = this.buildResponseMessage(categoryModel);

            if (categoryModel != null) {
                responseMessage.httpStatus = HttpStatus.CREATED;
                responseMessage.message = "Category save successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY;
                responseMessage.message = "Failed to save category";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("saveCategory -> save got exception");
        }
        return responseMessage;
    }

    public ResponseMessage updateCategory(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        CategoryModel categoryModel;
        try {
            categoryModel = Core.processRequestMessage(requestMessage, CategoryModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            categoryModel = this.update(categoryModel);
            responseMessage = this.buildResponseMessage(categoryModel);

            if (categoryModel != null) {
                responseMessage.httpStatus = HttpStatus.OK;
                responseMessage.message = "Category update successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY;
                responseMessage.message = "Failed to update category";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            //this.rollBack();
            log.error("updateCategory -> got exception");
        }
        return responseMessage;
    }


    public ResponseMessage deleteCategory(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        CategoryModel categoryModel;
        try {
            categoryModel = Core.processRequestMessage(requestMessage, CategoryModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            categoryModel = this.softDelete(categoryModel);
            responseMessage = this.buildResponseMessage(categoryModel);

            if (categoryModel != null) {
                responseMessage.httpStatus = HttpStatus.OK;
                responseMessage.message = "Category deleted successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY;
                responseMessage.message = "Failed to deleted category";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            //this.rollBack();
            log.error("deleteCategory -> got exception");
        }
        return responseMessage;
    }

    public ResponseMessage getByCategoryId(UUID id) {
        ResponseMessage responseMessage;
        CategoryModel categoryModel;

        try {
            categoryModel=this.getById(id);

            responseMessage = buildResponseMessage(categoryModel);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.FOUND;
                responseMessage.message = "Get requested category successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND;
                responseMessage.message = "Failed to requested category";
            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            //this.rollBack();
            ex.printStackTrace();
            log.error("getByCategoryId -> got exception");
        }

        return responseMessage;
    }


    public ResponseMessage getAllCategory(RequestMessage requestMessage) {
        ResponseMessage responseMessage = null;
        List<CategoryModel> list;

        try {
            Core.processRequestMessage(requestMessage);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            list = this.getAll();

            responseMessage = this.buildResponseMessage(list);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.FOUND;
                responseMessage.message = "Get all category successfully";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND;
                responseMessage.message = "Failed to get category";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            //this.rollBack();
            log.error("getAllCategory -> save got exception");
        }
        return responseMessage;
    }
}
