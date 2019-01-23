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
@Table(name = "invoice_history_view")
public class InvoiceHistoryView {
    @Id
    @Column(name = "invoice_id")
    private String invoiceId;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "invoice_no")
    private String invoiceNo;

    @Column(name = "invoice_amount")
    private Double invoiceAmount;

    @Column(name = "invoice_status")
    private String invoiceStatus;
}
