package response.soft.entities.view;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Entity
@Immutable
@Table(name = "available_stock_view")
public class AvailableStockView {
    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "store_id")
    private UUID storeId;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "available_qty")
    private Integer availableQty;
}
