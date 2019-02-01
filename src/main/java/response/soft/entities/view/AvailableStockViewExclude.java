/*
package response.soft.entities.view;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
@IdClass(AvailableStockViewExclude.class)
@Immutable
@Table(name = "available_stock_view")
public class AvailableStockViewExclude implements Serializable {
    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Id
    @Column(name = "category_id")
    private UUID categoryId;

    @Id
    @Column(name = "store_id")
    private UUID storeId;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "model_no")
    private String modelNo;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "available_qty")
    private Integer availableQty;
}
*/