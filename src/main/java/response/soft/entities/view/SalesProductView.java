package response.soft.entities.view;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
@Immutable
@Table(name = "sales_product_view")
public class SalesProductView implements Serializable {
    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "store_id")
    private UUID storeId;

    @Id
    @Column(name = "available")
    private Integer available;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "brand_name")
    private String brandName;

    @Id
    @Column(name = "model_no")
    private String modelNo;

    @Id
    @Column(name = "buy_price")
    private Double buyPrice;

    @Column(name = "sale_price")
    private Double salePrice;

    @Column(name = "description")
    private String description;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "image")
    private String image;

    @Id
    @Column(name = "vendor_id")
    private UUID vendorId;

    @Column(name = "vendor_name")
    private String vendorName;



}
