package response.soft.services;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.entities.Product;
import response.soft.model.ProductModel;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService extends BaseService<Product> {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(Product.class);
        Core.runTimeModelType.set(ProductModel.class);
    }

    public ResponseMessage saveProduct(RequestMessage requestMessage) {
        ResponseMessage responseMessage;// = new ResponseMessage();
        ProductModel productModel;
        byte[] imageByte;
        ProductModel searchDuplicateProductModel;
        List<ProductModel> foundDuplicateProduct;
        try {
            productModel = Core.processRequestMessage(requestMessage, ProductModel.class);
            // "name", "category_id", "model_no", "brand", "barcode"
            // search for duplicate product
            if(productModel!=null && !ObjectUtils.isEmpty(productModel)){
                searchDuplicateProductModel = new ProductModel();
                searchDuplicateProductModel.setName(productModel.getName());
                //searchDuplicateProductModel.setCategoryId(productModel.getCategoryId());
                searchDuplicateProductModel.setBrand(productModel.getBrand());
                searchDuplicateProductModel.setModelNo(productModel.getModelNo());
                searchDuplicateProductModel.setBarcode(productModel.getBarcode());

                foundDuplicateProduct= this.getAllByLikeORCondition(searchDuplicateProductModel);
                if(foundDuplicateProduct.size()!=0){
                   responseMessage = this.buildResponseMessage();
                   responseMessage.httpStatus= HttpStatus.CONFLICT.value();
                   responseMessage.message="Duplicate product found";
                   return responseMessage;
                }
            }


            imageByte= Base64.decodeBase64(productModel.getBase64ImageString());
            productModel.setImage(imageByte);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(productModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            productModel = this.save(productModel);
            //productModel.setBase64ImageString(new String(productModel.getImage()));
            responseMessage = this.buildResponseMessage(productModel);

            if (productModel != null) {
                responseMessage.httpStatus = HttpStatus.CREATED.value();
                responseMessage.message = "Product save successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to save Product";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("saveProduct -> save got exception");
        }
        return responseMessage;
    }

    public ResponseMessage updateProduct(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        ProductModel productModel;
        byte[] imageByte;
        try {


            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(ProductModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/



            productModel = Core.processRequestMessage(requestMessage, ProductModel.class);
            imageByte= Base64.decodeBase64(productModel.getBase64ImageString());
            productModel.setImage(imageByte);

            productModel = this.update(productModel);
            responseMessage = this.buildResponseMessage(productModel);

            if (productModel != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Product update successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to update Product";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("updateProduct -> got exception");
        }
        return responseMessage;
    }


    public ResponseMessage deleteProduct(UUID id) {
        ResponseMessage responseMessage;
        ProductModel productModel;
        Integer numberOfDeletedRow;
        try {
            //ProductModel = Core.processRequestMessage(requestMessage, ProductModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(ProductModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/
            productModel=this.getById(id);
            //ProductModel = this.softDelete(ProductModel);

            numberOfDeletedRow=this.deleteSoft(id);

            responseMessage = this.buildResponseMessage(numberOfDeletedRow);

            if (productModel != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Product deleted successfully!";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to deleted Product";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("deleteProduct -> got exception");
        }
        return responseMessage;
    }

    public ResponseMessage getByProductId(UUID id) {
        ResponseMessage responseMessage;
        ProductModel productModel;
        //String base64textString[];

        try {
            productModel=this.getById(id);
            //productModel.setImage(Base64.decodeBase64(productModel.getImage()));

            responseMessage = buildResponseMessage(productModel);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get requested Product successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to requested Product";
            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            //this.rollBack();
            ex.printStackTrace();
            log.error("getByProductId -> got exception");
        }

        return responseMessage;
    }


    public ResponseMessage getAllProduct(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        List<ProductModel> list;
        try {
            Core.processRequestMessage(requestMessage);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(ProductModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            list = this.getAll();

            responseMessage = this.buildResponseMessage(list);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get all Product successfully";
                //this.commit();
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get Product";
                //this.rollBack();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            //this.rollBack();
            log.error("getAllProduct -> save got exception");
        }
        return responseMessage;
    }

    public void saveImage(byte[] image){
        ProductModel productModel = new ProductModel();
        productModel.setName("Name");
        productModel.setImage(image);

        try {
            this.save(productModel);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
