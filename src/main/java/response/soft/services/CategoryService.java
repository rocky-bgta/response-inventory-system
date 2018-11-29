package response.soft.services;

import org.springframework.stereotype.Service;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.core.RequestObject;
import response.soft.core.ResponseObject;
import response.soft.entities.Category;
import response.soft.model.CategoryModel;

import java.util.List;

@Service
public class CategoryService  extends BaseService<Category> {

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(Category.class);
        Core.runTimeModelType.set(CategoryModel.class);
    }


    public ResponseObject save(RequestObject requestObject){
        ResponseObject responseObject = new ResponseObject();
        CategoryModel categoryModel;
        try {
            categoryModel = Core.processRequestObject(requestObject,CategoryModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            categoryModel = this.save(categoryModel);

            responseObject.data = categoryModel;
          /*  if(categoryModel != null)
            {
                responseObject.responseCode = HttpConstant.SUCCESS_CODE;
                responseObject.message = MessageConstant.COUNTRY_SAVED_SUCCESSFULLY;
                this.commit();
            }else
            {
                responseObject.responseCode = HttpConstant.FAILED_ERROR_CODE;
                responseObject.message = MessageConstant.COUNTRY_SAVED_FAILED;
                this.rollBack();
            }*/
        }catch (Exception ex){
            //responseObject = this.getDefaultResponseMessage(requestObject.requestObj, HttpConstant.INTERNAL_SERVER_ERROR, HttpConstant.UN_PROCESSABLE_REQUEST);

            //this.rollBack();
            //log.error("CountryServiceManager -> save got exception");
        }
        return responseObject;
    }

    public ResponseObject getAllCategory(RequestObject requestObject){
        ResponseObject responseObject=null;
        List<CategoryModel> list;
        try {
            Core.processRequestObject(requestObject);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            list = this.getAll();

            responseObject = this.buildResponseObject(list);

          /*  if(categoryModel != null)
            {
                responseObject.responseCode = HttpConstant.SUCCESS_CODE;
                responseObject.message = MessageConstant.COUNTRY_SAVED_SUCCESSFULLY;
                this.commit();
            }else
            {
                responseObject.responseCode = HttpConstant.FAILED_ERROR_CODE;
                responseObject.message = MessageConstant.COUNTRY_SAVED_FAILED;
                this.rollBack();
            }*/
        }catch (Exception ex){
            //responseObject = this.getDefaultResponseMessage(requestObject.requestObj, HttpConstant.INTERNAL_SERVER_ERROR, HttpConstant.UN_PROCESSABLE_REQUEST);

            //this.rollBack();
            //log.error("CountryServiceManager -> save got exception");
        }
        return responseObject;
    }
}
