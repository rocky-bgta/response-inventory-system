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
@Table(name = "due_payment")
public class DuePayment extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "sales_balance_id")
    @NotNull
    private UUID salesBalanceId;

    @Column(name = "customer_id")
    @NotNull
    private UUID customerId;

    @Column(name = "product_id")
    @NotNull
    private UUID productId;

    @Column(name = "due")
    @NotNull
    private Double due;

    @Column(name = "paid")
    private Double paid;

    @Column(name = "paid_status")
    private Integer paidStatus;

    @Column(name = "date")
    @NotNull
    private Date date;

}
