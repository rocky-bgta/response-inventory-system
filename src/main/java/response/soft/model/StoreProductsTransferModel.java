package response.soft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import response.soft.core.BaseModel;

import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode
public class StoreProductsTransferModel extends BaseModel {

    private UUID id;
    private UUID stockId;
    private UUID fromStoreId;
    private UUID toStoreId;
    private UUID vendorId;
    private UUID productId;
    private Date date;
    private String reason;
}
