package response.soft.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import response.soft.core.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode
@DynamicInsert
@DynamicUpdate
@Table(name = "stock")
public class Stock extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "store_id")
    @NotNull
    private UUID storeId;

    @Column(name = "product_id")
    @NotNull
    private UUID productId;

    @Column(name = "in_out")
    @NotNull
    private Integer inOut;

    @Column(name = "quantity")
    @NotNull
    private Integer quantity;

    @Column(name = "unit_price")
    @NotNull
    private Double unitPrice;

    @Column(name = "total")
    @NotNull
    private Double total;

    @Column(name = "date")
    private Date date;

}
