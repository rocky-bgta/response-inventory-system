package response.soft.model.view;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class StoreInProductsViewModel {
    private String storeName;
    private UUID storeId;
    private String vendorName;
    private UUID vendorId;
    private String productName;
    private UUID productId;
    private String barcode;
    private String serialNo;
    private Double price;
    private Integer quantity;
    private Double totalPrice;
    private Date entryDate;
    private Date manufacturingDate;
    private Date expirationDate;
}
