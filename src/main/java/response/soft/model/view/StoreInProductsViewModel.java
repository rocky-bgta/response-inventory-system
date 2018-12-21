package response.soft.model.view;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class StoreInProductsViewModel {
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
