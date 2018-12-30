
package response.soft.model.view;

import lombok.Data;
import lombok.EqualsAndHashCode;
import response.soft.core.BaseModel;

import java.util.UUID;

@Data
@EqualsAndHashCode
public class SalesProductViewModel extends BaseModel {
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

    //=========================
    private Double salesPrice;
    private Integer salesQty;
    private String serialNo;
    private Integer supportPeriodInMonth;
    private Double totalPrice;
    private UUID stockId;
    private UUID stockInProductId;



}

