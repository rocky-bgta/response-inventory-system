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
@Table(name = "customer_payment")
public class CustomerPayment extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "customer_id")
    @NotNull
    private UUID customerId;

    @Column(name = "invoice_no")
    @NotNull
    private String invoiceNo;

    @Column(name = "paid_amount")
    private Double paidAmount;

    @Column(name = "due_amount")
    private Double dueAmount;

    @Column(name = "grand_amount")
    @NotNull
    private Double grandTotal;

    @Column(name = "paid_status")
    @NotNull
    private Integer paidStatus;

    @Column(name = "date")
    @NotNull
    private Date date;

}
