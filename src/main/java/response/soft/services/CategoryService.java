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


    public ResponseObject save(RequestObject requestMessage){
        ResponseObject responseMessage = new ResponseObject();
        CategoryModel categoryModel;
        try {
            categoryModel = Core.getRequestObject(requestMessage,CategoryModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            categoryModel = this.save(categoryModel);

            responseMessage.data = categoryModel;
          /*  if(categoryModel != null)
            {
                responseMessage.responseCode = HttpConstant.SUCCESS_CODE;
                responseMessage.message = MessageConstant.COUNTRY_SAVED_SUCCESSFULLY;
                this.commit();
            }else
            {
                responseMessage.responseCode = HttpConstant.FAILED_ERROR_CODE;
                responseMessage.message = MessageConstant.COUNTRY_SAVED_FAILED;
                this.rollBack();
            }*/
        }catch (Exception ex){
            //responseMessage = this.getDefaultResponseMessage(requestMessage.requestObj, HttpConstant.INTERNAL_SERVER_ERROR, HttpConstant.UN_PROCESSABLE_REQUEST);

            //this.rollBack();
            //log.error("CountryServiceManager -> save got exception");
        }
        return responseMessage;
    }

    public ResponseObject getAllCategory(RequestObject requestMessage){
        ResponseObject responseMessage=null;
        List<CategoryModel> list;
        try {
            Core.getRequestObject(requestMessage,CategoryModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            list = this.getAll();

            responseMessage = this.buildResponseObject(list);

          /*  if(categoryModel != null)
            {
                responseMessage.responseCode = HttpConstant.SUCCESS_CODE;
                responseMessage.message = MessageConstant.COUNTRY_SAVED_SUCCESSFULLY;
                this.commit();
            }else
            {
                responseMessage.responseCode = HttpConstant.FAILED_ERROR_CODE;
                responseMessage.message = MessageConstant.COUNTRY_SAVED_FAILED;
                this.rollBack();
            }*/
        }catch (Exception ex){
            //responseMessage = this.getDefaultResponseMessage(requestMessage.requestObj, HttpConstant.INTERNAL_SERVER_ERROR, HttpConstant.UN_PROCESSABLE_REQUEST);

            //this.rollBack();
            //log.error("CountryServiceManager -> save got exception");
        }
        return responseMessage;
    }
}
