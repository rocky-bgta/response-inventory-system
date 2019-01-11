
package response.soft.model.view;

import lombok.Data;
import lombok.EqualsAndHashCode;
import response.soft.core.BaseModel;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode
public class ProductSalesViewModel extends BaseModel {
    private UUID storeId;
    private UUID customerId;
    private List<SalesProductViewModel> salesProductViewModelList;
    private String barcode;
    private String serialNo;
    private Integer salesMethod;
    private Double paidAmount;
    private Double dueAmount;
    private Double grandTotal;
    private String invoiceNo;


}

