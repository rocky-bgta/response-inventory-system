package response.soft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import response.soft.core.BaseModel;

import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode
public class CustomerPaymentModel extends BaseModel {
    private UUID id;
    private UUID customerId;
    private String invoiceNo;
    private Double paidAmount;
    private Double dueAmount;
    private Double grandTotal;
    private Integer paidStatus;
    private Date invoiceDate;
    private Date paymentDate;

    //========================
    private String customerName;
    private Double currentPayment;
}
