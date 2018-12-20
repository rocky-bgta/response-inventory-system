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
@Table(name = "product_sales")
public class ProductSales extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "store_out_id")
    @NotNull
    private UUID storeOutId;

    @Column(name = "product_id")
    @NotNull
    private UUID productId;

    @Column(name = "customer_id")
    @NotNull
    private UUID customerId;

    @Column(name = "buy_price")
    @NotNull
    private Double buyPrice;

    @Column(name = "sales_type")
    @NotNull
    private Integer salesType;

    @Column(name = "sales_price")
    @NotNull
    private Double salesPrice;

    @Column(name = "date")
    @NotNull
    private Date date;

    @Column(name = "support_period")
    private Integer supportPeriod;

}
