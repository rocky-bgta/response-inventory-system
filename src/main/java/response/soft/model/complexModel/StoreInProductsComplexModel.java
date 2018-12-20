package response.soft.model.complexModel;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class StoreInProductsComplexModel {
    private UUID vendorId;
    private UUID storeId;
    private UUID productId;
    private String barcode;
    private Integer quantity;
    private Double unitPrice;
    private Date entryDate;
    private Date MFDate;
    private Date EXPDate;
}
