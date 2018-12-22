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
        //byte[] imageByte;
        ProductModel searchDuplicateProductModel;
        List<ProductModel> foundDuplicateProduct;
        try {
            productModel = Core.processRequestMessage(requestMessage, ProductModel.class);
            // "name", "category_id", "model_no", "brand", "barcode"
            // search for duplicate product
            if (productModel != null && !ObjectUtils.isEmpty(productModel)) {
                searchDuplicateProductModel = new ProductModel();
                searchDuplicateProductModel.setName(productModel.getName());
                searchDuplicateProductModel.setCategoryId(productModel.getCategoryId());
                searchDuplicateProductModel.setBrandId(productModel.getBrandId());
                searchDuplicateProductModel.setModelNo(productModel.getModelNo());
                searchDuplicateProductModel.setBarcode(productModel.getBarcode());

                foundDuplicateProduct = this.getAllByConditionWithActive(searchDuplicateProductModel);
                if (foundDuplicateProduct.size() != 0) {
                    responseMessage = this.buildResponseMessage();
                    responseMessage.httpStatus = HttpStatus.CONFLICT.value();
                    responseMessage.message = "Duplicate product found";
                    return responseMessage;
                }
            }

/*
            if (productModel.getBase64ImageString() != null && productModel.getBase64ImageString().length() > 0) {
                imageByte = Base64.decodeBase64(productModel.getBase64ImageString());
                productModel.setImage(imageByte);
            }*/

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(productModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            //productModel = Core.getTrimmedModel(productModel);
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

    public ResponseMessage getProductByBarcode(String barcode){
        ResponseMessage responseMessage;
        ProductModel whereConditionProductModel, foundProduct;
        List<ProductModel> productModelList;
        //Integer numberOfDeletedRow;
        try {
            //ProductModel = Core.processRequestMessage(requestMessage, ProductModel.class);

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(ProductModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            whereConditionProductModel = new ProductModel();
            whereConditionProductModel.setBarcode(barcode.trim());
            productModelList = this.getAllByConditionWithActive(whereConditionProductModel);

            if (productModelList != null && productModelList.size()==1) {
                foundProduct = productModelList.get(0);
                responseMessage = this.buildResponseMessage(foundProduct);
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Product found successfully";

            } else {
                responseMessage = this.buildResponseMessage();
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Product not found in current stock";

            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            log.error("getProductByBarcode -> got exception");
        }
        return responseMessage;
    }

    public ResponseMessage updateProduct(RequestMessage requestMessage) {
        ResponseMessage responseMessage=null;
        ProductModel requestedProductModel,
                searchDuplicateProductModel,
                oldProductModel,
                updatedProductModel;
        List<ProductModel> foundDuplicateProduct;
        int countPropertyValueDifference;
        int acceptedUpdatePropertyDifference=3;
        //byte[] imageByte;
        try {


            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(ProductModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/


            requestedProductModel = Core.processRequestMessage(requestMessage, ProductModel.class);

            // retrieved old store to update created and created date.
            oldProductModel = this.getByIdActiveStatus(requestedProductModel.getId());


            // "name", "category_id", "model_no", "brand", "barcode"
            // search for duplicate product
            if (requestedProductModel != null && !ObjectUtils.isEmpty(requestedProductModel)) {
                searchDuplicateProductModel = new ProductModel();
                searchDuplicateProductModel.setName(requestedProductModel.getName());
                searchDuplicateProductModel.setCategoryId(requestedProductModel.getCategoryId());
                searchDuplicateProductModel.setBrandId(requestedProductModel.getBrandId());
                searchDuplicateProductModel.setModelNo(requestedProductModel.getModelNo());
                searchDuplicateProductModel.setBarcode(requestedProductModel.getBarcode());
                foundDuplicateProduct = this.getAllByConditionWithActive(searchDuplicateProductModel);

                if (foundDuplicateProduct.size() == 0) {
                    updatedProductModel = this.update(requestedProductModel,oldProductModel);
                    responseMessage = this.buildResponseMessage(updatedProductModel);
                    responseMessage.httpStatus = HttpStatus.OK.value();
                    responseMessage.message = "Successfully Product updated";
                    //this.commit();
                    return responseMessage;
                }

                if(foundDuplicateProduct.size()>0){

                    countPropertyValueDifference = Core.comparePropertyValueDifference(requestedProductModel,oldProductModel);
                    if(countPropertyValueDifference==acceptedUpdatePropertyDifference
                            || countPropertyValueDifference<acceptedUpdatePropertyDifference){
                        updatedProductModel = this.update(requestedProductModel,oldProductModel);
                        responseMessage = this.buildResponseMessage(updatedProductModel);
                        responseMessage.httpStatus = HttpStatus.OK.value();
                        responseMessage.message = "Successfully Product updated";
                        return responseMessage;
                    }else {
                        responseMessage = this.buildResponseMessage(requestedProductModel);
                        responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                        responseMessage.message = "Failed to update Product";
                        //this.rollBack();
                        return responseMessage;
                    }

                }
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
            productModel = this.getById(id);
            //ProductModel = this.softDelete(ProductModel);

            numberOfDeletedRow = this.deleteSoft(id);

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
            productModel = this.getByIdActiveStatus(id);
            //productModel.setImage(Base64.decodeBase64(productModel.getImage()));

            responseMessage = buildResponseMessage(productModel);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
                responseMessage.message = "Get requested Product successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.CONFLICT.value();
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
        DataTableRequest dataTableRequest;
        StringBuilder queryBuilderString;
        String searchKey=null;
        try {
            Core.processRequestMessage(requestMessage);
            dataTableRequest = requestMessage.dataTableRequest;

            if(dataTableRequest!=null) {
                searchKey = dataTableRequest.search.value;
                searchKey = searchKey.trim().toLowerCase();
            }

            if (dataTableRequest != null && !StringUtils.isEmpty(searchKey)) {
                //implement full-text search
                queryBuilderString = new StringBuilder();
                queryBuilderString.append("SELECT p.id, ")
                        .append("p.name, ")
                        .append("c.id, ")
                        .append("p.brandId, ")
                        .append("p.modelNo, ")
                        .append("CAST(p.price AS string), ")
                        .append("p.description, ")
                        .append("p.barcode, ")
                        .append("p.image ")
                        .append("FROM Product p ")
                        .append("LEFT JOIN Category c ON p.categoryId = c.id  ")
                        .append("LEFT JOIN Brand b ON p.brandId = b.id  ")
                        .append("WHERE ")
                        .append("( ")
                        .append("lower(p.name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(c.name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(b.name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(p.modelNo) LIKE '%" + searchKey + "%' ")
                        .append("OR CAST(p.price AS string) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(p.description) LIKE '%" + searchKey + "%' ")
                        .append(") ")
                        .append("AND p.status="+SqlEnum.Status.Active.get());

                list = this.executeHqlQuery(queryBuilderString.toString(), ProductModel.class, SqlEnum.QueryType.Join.get());

            } else {
                list = this.getAll();
            }



            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(ProductModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/


            responseMessage = this.buildResponseMessage(list);

            if (responseMessage.data != null) {
                responseMessage.httpStatus = HttpStatus.OK.value();
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

    /*public void saveImage(byte[] image) {
        ProductModel productModel = new ProductModel();
        productModel.setName("Name");
        productModel.setImage(image);
        try {
            this.save(productModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
