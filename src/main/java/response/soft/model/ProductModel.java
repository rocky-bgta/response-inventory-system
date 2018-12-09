
package response.soft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import response.soft.core.BaseEntity;
import response.soft.core.BaseModel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@EqualsAndHashCode
public class ProductModel extends BaseModel {

    private UUID id;
    private String name;
    private UUID categoryId;
    private String brand;
    private String modelNo;
    //private String serialNo;
    private Double price;
    private String description;
    private String barcode;
    private byte[] image;

    private String base64ImageString;

}

