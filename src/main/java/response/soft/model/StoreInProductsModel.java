package response.soft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import response.soft.core.BaseModel;

import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode
public class StoreInProductsModel extends BaseModel {
    private UUID id;
    private UUID stockId;
    private UUID storeId;
    private UUID productId;
    private UUID vendorId;
    private Double price;
    private Integer productStatus;
    private Date entryDate;
    private Date manufacturingDate;
    private Date expirationDate;
}
