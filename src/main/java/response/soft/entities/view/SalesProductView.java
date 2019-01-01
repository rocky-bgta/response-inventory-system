package response.soft.entities.view;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.Data;

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
    private UUID product_id;
    private UUID store_id;
    private Integer available;
    private String product_name;
    private String category_name;
    private String brand_name;
    private String model_no;
    private Double buy_price;
    private String description;
    private String barcode;
    private String image;
}
