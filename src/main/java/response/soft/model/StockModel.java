package response.soft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import response.soft.core.BaseModel;

import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode
public class StockModel extends BaseModel {
    private UUID id;
    private UUID storeId;
    private UUID productId;
    private Integer inOut;
    private Integer quantity;
    private Double unitPrice;
    private Double total;
    private Date date;
}
