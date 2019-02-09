package response.soft.model.view;

import lombok.Data;
import response.soft.entities.view.AvailableStockView;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class StockViewModel {
    private UUID storeId;
    private UUID categoryId;
    private UUID productId;
    private Integer availableQty;
    private Integer updateQty;
    private Double unitPrice;
    private Double totalPrice;
    private Date fromDate;
    private Date toDate;
    private List<AvailableStockView> availableStockViewList;
    private Double totalStockProductPrice;
    private List<SalesProductViewModel> stockProductListForUpdate;
}
