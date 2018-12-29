
package response.soft.model.view;

import lombok.Data;
import lombok.EqualsAndHashCode;
import response.soft.core.BaseModel;

import java.util.UUID;

@Data
@EqualsAndHashCode
public class ProductViewModel extends BaseModel {
    private UUID productId;
    private Integer available;
    private String productName;
    private String categoryName;
    private String brandName;
    private String modelNo;
    private Double buyPrice;
    private String description;
    private String barcode;
    private String image;
}
