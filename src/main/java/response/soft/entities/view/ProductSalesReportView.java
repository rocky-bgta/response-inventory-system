package response.soft.entities.view;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Immutable
@Table(name = "product_sales_report_view")
public class ProductSalesReportView {
    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "invoice_no")
    private String invoiceNo;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "buy_price")
    private Double buyPrice;

    @Column(name = "sales_price")
    private Double salesPrice;

    @Column(name = "profit")
    private Double profit;

    @Column(name = "date")
    private Date date;


}
