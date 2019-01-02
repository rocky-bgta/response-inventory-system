package response.soft.entities.view;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Data
@Immutable
@Table(name = "sales_product_view")
public class SalesProductView {
    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "store_id")
    private UUID storeId;

    @Column(name = "available")
    private Integer available;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "model_no")
    private String modelNo;

    @Column(name = "buy_price")
    private Double buyPrice;

    @Column(name = "description")
    private String description;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "image")
    private String image;
}
