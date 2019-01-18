package response.soft.entities.view;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@IdClass(ProductSalesReportView.class)
@Immutable
@Table(name = "product_sales_report_view")
public class ProductSalesReportView implements Serializable {
    @Id
    @Column(name = "invoice_no")
    private String invoiceNo;

    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "product_name")
    private String productName;

    @Id
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
