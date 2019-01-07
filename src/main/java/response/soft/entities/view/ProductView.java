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
@Table(name = "product_view")
public class ProductView {

    @Id
    @Column(name = "id")
    private UUID Id;

    @Column(name = "name")
    private String name;

    @Column(name = "category")
    private String category;

    @Column(name = "brand")
    private String brand;

    @Column(name = "model_no")
    private String modelNo;

    @Column(name = "price")
    private Double price;

    @Column(name = "image")
    private String image;
}
