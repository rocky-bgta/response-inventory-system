package response.soft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import response.soft.core.BaseModel;

import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode
public class StoreOutProductModel extends BaseModel {
    private UUID id;
    private UUID stockId;
    private UUID storeId;
    private UUID storeInProductId;
    private UUID productId;
    private Date date;

}
