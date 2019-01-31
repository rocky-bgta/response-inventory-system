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
import response.soft.entities.Product;
import response.soft.entities.view.ProductView;
import response.soft.model.ProductModel;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService extends BaseService<Product> {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    /*@Autowired
    public ProductService(ApplicationContext applicationContext) {
        ApplicationContext applicationContext1 = applicationContext;
    }*/

    //EntityManagerFactory entityManagerFactory;


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
                Core.commitTransaction();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to save Product";
                Core.rollBackTransaction();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            Core.rollBackTransaction();
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
                    Core.commitTransaction();
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
                        Core.commitTransaction();
                        return responseMessage;
                    }else {
                        responseMessage = this.buildResponseMessage(requestedProductModel);
                        responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                        responseMessage.message = "Failed to update Product";
                        Core.rollBackTransaction();
                        return responseMessage;
                    }

                }
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            Core.rollBackTransaction();
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
                Core.commitTransaction();
            } else {
                responseMessage.httpStatus = HttpStatus.FAILED_DEPENDENCY.value();
                responseMessage.message = "Failed to deleted Product";
                Core.rollBackTransaction();
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            Core.rollBackTransaction();
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
            ex.printStackTrace();
            log.error("getByProductId -> got exception");
        }

        return responseMessage;
    }

    public ResponseMessage getByProductBarcode(String barcode) {
        ResponseMessage responseMessage;
        ProductModel productModel, whereCondition;
        List<ProductModel> productModelList;
        //String base64textString[];

        try {
            whereCondition = new ProductModel();
            whereCondition.setBarcode(barcode);
            productModelList = this.getAllByConditionWithActive(whereCondition);

            if(productModelList.size()>0){
                productModel = productModelList.get(0);
                responseMessage = buildResponseMessage(productModel);
                responseMessage.httpStatus=HttpStatus.FOUND.value();
                responseMessage.message="Requested product found";
            }else {
                responseMessage = buildFailedResponseMessage("Requested product not found");
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
            }

        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            log.error("getByProductId -> got exception");
        }
        return responseMessage;
    }



    public ResponseMessage getAllProduct(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        List<ProductModel> list;
        StringBuilder queryBuilderString;
        String searchKey;
        try {
            //this.resetPaginationVariable();
            Core.processRequestMessage(requestMessage);
            searchKey = Core.dataTableSearchKey.get();

            if(searchKey!=null) {
                searchKey = searchKey.trim().toLowerCase();
            }

            if (searchKey != null && !StringUtils.isEmpty(searchKey)) {
                //implement full-text search
                queryBuilderString = new StringBuilder();
                queryBuilderString.append("SELECT p.id, ")
                        .append("p.name, ")
                        .append("c.id, ")
                        .append("p.brandId, ")
                        .append("p.modelNo, ")
                        .append("p.price, ")
                        .append("p.salePrice, ")
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
                        .append("OR CAST(p.salePrice AS string) LIKE '%" + searchKey + "%' ")
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
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get Product";
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            log.error("getAllProduct -> save got exception");
        }
        return responseMessage;
    }

    public ResponseMessage getProductViewList(RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        List<ProductView> list;
        String searchKey;
        StringBuilder queryBuilderString =new StringBuilder();
        try {

            Core.processRequestMessage(requestMessage);
            searchKey = Core.dataTableSearchKey.get();
            if(searchKey!=null && !StringUtils.equals(searchKey,"string")) {
                searchKey = searchKey.trim().toLowerCase();
            }

            /*Set<ConstraintViolation<CountryModel>> violations = this.validator.validate(categoryModel);
            for (ConstraintViolation<CountryModel> violation : violations) {
                log.error(violation.getMessage());
            }*/

            //============ full text search ===========================================

            if ((searchKey != null && !StringUtils.isEmpty(searchKey))) {

                queryBuilderString.append("SELECT v ")
                        .append("FROM ProductView v ")
                        .append("WHERE ")
                        .append("lower(v.name) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.category) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.brand) LIKE '%" + searchKey + "%' ")
                        .append("OR lower(v.modelNo) LIKE '%" + searchKey + "%' ")
                        .append("OR CAST(v.price AS string) LIKE '%" + searchKey + "%' ")
                        .append("OR CAST(v.salePrice AS string) LIKE '%" + searchKey + "%' ");

                list = this.executeHqlQuery(queryBuilderString.toString(),ProductView.class,SqlEnum.QueryType.View.get());
                //============ full text search ===========================================
            }else {
                queryBuilderString.setLength(0);
                queryBuilderString.append("SELECT v FROM ProductView v ");
                list = this.executeHqlQuery(queryBuilderString.toString(),ProductView.class,SqlEnum.QueryType.View.get());
            }

            responseMessage = this.buildResponseMessage(list);

            if (list != null && list.size()>0) {
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.message = "Get all Product View successfully";
            } else {
                responseMessage.httpStatus = HttpStatus.NOT_FOUND.value();
                responseMessage.message = "Failed to get Product View";
            }
        } catch (Exception ex) {
            responseMessage = this.buildFailedResponseMessage();
            ex.printStackTrace();
            log.error("getProductViewList -> get product view got exception");
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
