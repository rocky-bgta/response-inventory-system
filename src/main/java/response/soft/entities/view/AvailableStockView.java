package response.soft.entities.view;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@IdClass(AvailableStockView.class)
@Immutable
@Table(name = "available_stock_view")
public class AvailableStockView implements Serializable {

    @Id
    @Column(name = "store_id")
    private UUID storeId;

    @Id
    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "model_no")
    private String modelNo;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "available_qty")
    private Integer availableQty;

    @Column(name = "stock_date")
    private Date stockDate;
}
