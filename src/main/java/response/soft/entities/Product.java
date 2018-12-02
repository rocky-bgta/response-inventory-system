/*
package response.soft.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import response.soft.core.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Entity
@Table(name = "product")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "category")
    private UUID category;

    @Column(name = "model_no")
    private Double modelNo;

    @Column(name = "price")
    private Double price;

    @Column(name = "description")
    private String description;

    @Column(name = "barcode")
    private String barcode;

    @Lob
    @Column(name = "image_path")
    private byte imagePath;

}
*/
