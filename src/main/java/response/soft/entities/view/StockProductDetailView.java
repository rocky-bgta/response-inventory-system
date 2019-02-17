package response.soft.entities.view;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Immutable
@Table(name = "stock_product_details_view")
public class StockProductDetailView implements Serializable {
    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "product_name")
    private String productName;

    @Id
    @Column(name = "store_id")
    private UUID storeId;

    @Id
    @Column(name = "vendor_id")
    private UUID vendorId;

    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "brand_name")
    private String brandName;

    @Id
    @Column(name = "model_no")
    private String modelNo;

    @Id
    @Column(name = "stock_qty")
    private Integer stockQty;

    @Id
    @Column(name = "buy_price")
    private Double buyPrice;

    @Id
    @Column(name = "sale_price")
    private Double salePrice;

    @Id
    @Column(name = "entry_date")
    private Date entryDate;

}
