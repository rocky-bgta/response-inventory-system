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
@Table(name = "customer_payment_history")
public class CustomerPaymentHistory extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;


    @Column(name = "invoice_no")
    @NotNull
    private String invoiceNo;

    @Column(name = "customer_id")
    @NotNull
    private UUID customerId;

    @Column(name = "paid_amount")
    @NotNull
    private Double paidAmount;

    @Column(name = "payment_date")
    @NotNull
    private Date paymentDate;

}
