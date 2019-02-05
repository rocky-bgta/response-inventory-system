package response.soft.entities.view;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
@IdClass(AvailableStockView.class)
@Immutable
@Table(name = "available_stock_view")
public class AvailableStockView implements Serializable {

    @Id
    @Column(name = "store_id")
    private UUID store_id;

    @Id
    @Column(name = "store_name")
    private String store_name;

    @Id
    @Column(name = "category_id")
    private UUID category_id;

    @Column(name = "category_name")
    private String category_name;

    @Id
    @Column(name = "product_id")
    private UUID product_id;

    @Column(name = "product_name")
    private String product_name;

    @Column(name = "model_no")
    private String model_no;

    @Column(name = "total_price")
    private Double total_price;

    @Column(name = "available_qty")
    private Integer available_qty;

   /* @Column(name = "stock_date")
    private Date stockDate;*/
}
