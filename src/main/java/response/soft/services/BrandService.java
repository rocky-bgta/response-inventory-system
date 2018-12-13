package response.soft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.entities.Brand;
import response.soft.model.BrandModel;

import java.util.List;
import java.util.UUID;

@Service
public class BrandService extends BaseService<Brand> {

    private static final Logger log = LoggerFactory.getLogger(BrandService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(Brand.class);
        Core.runTimeModelType.set(BrandModel.class);
    }

    public ResponseMessage saveBrand(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        BrandModel brandModel;
        try {
            brandModel = Core.processRequestMessage(requestMessage, BrandModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            brandModel = this.save(brandModel);
            responseMessage = this.buildResponseMessage(brandModel);

            if (brandModel != null) {
                responseMessage.httpStatus = HttpStatus.CREATED.value();
                responseMessage.message = "Brand save successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to save category";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("saveBrand -> save got exception");
        }
        return responseMessage;
    }

    public ResponseMessage updateBrand(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        BrandModel brandModel;
        try {
            brandModel = Core.processRequestMessage(requestMessage, BrandModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            brandModel = this.update(brandModel);
            responseMessage = this.buildResponseMessage(brandModel);

            if (brandModel != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Brand update successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to update category";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("updateBrand -> got exception");
        }
        return responseMessage;
    }


    public ResponseMessage deleteBrand(UUID id) {
        ResponseMessage responseMessage;
        BrandModel brandModel;
        Integer numberOfDeletedRow;
        try {
            //categoryModel = Core.processRequestMessage(requestMessage, BrandModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/
            brandModel=this.getById(id);
            //categoryModel = this.softDelete(categoryModel);

            numberOfDeletedRow=this.deleteSoft(id);

            responseMessage = this.buildResponseMessage(numberOfDeletedRow);

            if (brandModel != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Brand deleted successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to deleted category";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("deleteBrand -> got exception");
        }
        return responseMessage;
    }

    public ResponseMessage getByBrandId(UUID id) {
        ResponseMessage responseMessage;
        BrandModel brandModel;

        try {
            brandModel=this.getById(id);

            responseMessage = buildResponseMessage(brandModel);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get requested brand successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to requested category";
            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            //this.rollBack();
            ex.printStackTrace();
            log.error("getByBrandId -> got exception");
        }

        return responseMessage;
    }


    public ResponseMessage getAllBrand(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        List<BrandModel> list;

        try {
            Core.processRequestMessage(requestMessage);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            list = this.getAll();

            responseMessage = this.buildResponseMessage(list);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get all brand successfully";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get category";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("getAllBrand -> save got exception");
        }
        return responseMessage;
    }
}
