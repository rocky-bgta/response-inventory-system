
package response.soft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import response.soft.core.BaseModel;

import java.util.UUID;

@Data
@EqualsAndHashCode
public class ProductModel extends BaseModel {

    private UUID id;
    private String name;
    private UUID categoryId;
    private UUID brandId;
    private String modelNo;
    //private String serialNo;
    private Double price;
    private String description;
    private String barcode;
    //private byte[] image;
    private String image;

    //private String base64ImageString;

}

