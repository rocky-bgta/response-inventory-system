
package response.soft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import response.soft.core.BaseModel;

@Data
@EqualsAndHashCode
public class ProductModel extends BaseModel {

    private String id;
    private String name;
    private String categoryId;
    private String brand;
    private String modelNo;
    //private String serialNo;
    private Double price;
    private String description;
    private String barcode;
    private byte[] image;

    private String base64ImageString;

}

