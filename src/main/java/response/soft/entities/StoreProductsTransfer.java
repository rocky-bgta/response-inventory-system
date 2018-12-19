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
@Table(name = "store_products_transfer")
public class StoreProductsTransfer extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "stock_id")
    @NotNull
    private UUID stockId;

    @Column(name = "from_store_id")
    @NotNull
    private UUID fromStoreId;

    @Column(name = "to_store_id")
    @NotNull
    private UUID toStoreId;

    @Column(name = "vendor_id")
    @NotNull
    private UUID vendorId;

    @Column(name = "product_id")
    @NotNull
    private UUID productId;

    @Column(name = "date")
    @NotNull
    private Date date;

    @Column(name = "reason")
    private String reason;
}
