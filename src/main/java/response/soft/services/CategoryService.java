package response.soft.services;

import org.springframework.stereotype.Service;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.entities.Category;
import response.soft.model.CategoryModel;

@Service
public class CategoryService  extends BaseService<Category> {

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(Category.class);
        Core.runTimeModelType.set(CategoryModel.class);
    }


    public ResponseMessage save(RequestMessage requestMessage){
        ResponseMessage responseMessage = new ResponseMessage();
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
                responseMessage.responseCode = TillBoxAppConstant.SUCCESS_CODE;
                responseMessage.message = MessageConstant.COUNTRY_SAVED_SUCCESSFULLY;
                this.commit();
            }else
            {
                responseMessage.responseCode = TillBoxAppConstant.FAILED_ERROR_CODE;
                responseMessage.message = MessageConstant.COUNTRY_SAVED_FAILED;
                this.rollBack();
            }*/
        }catch (Exception ex){
            //responseMessage = this.getDefaultResponseMessage(requestMessage.requestObj, TillBoxAppConstant.INTERNAL_SERVER_ERROR, TillBoxAppConstant.UN_PROCESSABLE_REQUEST);

            //this.rollBack();
            //log.error("CountryServiceManager -> save got exception");
        }
        return responseMessage;
    }
}
