package response.soft.model.view;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode
public class SalesHistoryViewModel {
    private String invoiceNo;
    private UUID storeId;
    private String storeName;
    private UUID customerId;
    private String customerName;
    private UUID productId;
    private String productName;
    private Double salesPrice;
    private Double discount;

}
