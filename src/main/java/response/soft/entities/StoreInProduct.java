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
@Table(name = "store_in_product")
public class StoreInProduct extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "stock_id")
    @NotNull
    private UUID stockId;

    @Column(name = "store_id")
    @NotNull
    private UUID storeId;

    @Column(name = "product_id")
    @NotNull
    private UUID productId;

    @Column(name = "vendor_id")
    @NotNull
    private UUID vendorId;

    @Column(name = "price")
    @NotNull
    private Double price;

    @Column(name = "product_status")
    @NotNull
    private Integer productStatus;

    @Column(name = "entry_date")
    @NotNull
    private Date entryDate;

    @Column(name = "manufacturing_date")
    private Date manufacturingDate;

    @Column(name = "expiration_date")
    private Date expirationDate;


}
