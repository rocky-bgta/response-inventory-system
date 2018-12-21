
package response.soft.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import response.soft.core.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode
@DynamicInsert
@DynamicUpdate
@Table(name = "product",uniqueConstraints=
@UniqueConstraint(columnNames={"name", "category_id", "model_no", "brand_id", "barcode","status"}))
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "category_id")
    @NotNull
    private UUID categoryId;

    @Column(name = "brand_id")
    @NotNull
    private UUID brandId;

    @Column(name = "model_no")
    @NotNull
    private String modelNo;

   /*
    @Column(name = "serial_no")
    private String serialNo;
    */

    @Column(name = "price")
    private Double price;

    @Column(name = "description")
    private String description;

    @Column(name = "barcode", unique = true)
    @NotNull
    private String barcode;

    @Column(name = "image")
    @Type(type="text")
    private String image;

}

