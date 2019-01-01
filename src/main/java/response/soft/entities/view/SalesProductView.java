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
    private UUID productid;
    private UUID storeid;
    private Integer available;
    private String productname;
    private String categoryname;
    private String brandname;
    private String modelno;
    private Double buyprice;
    private String description;
    private String barcode;
    private String image;
}
