package response.soft.model.view;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode
public class CustomerDuePaymentHistoryViewModel {
    private UUID customerId;
    private String customerName;
    private String invoiceNo;
    private Double paidAmount;
    private Date paymentDate;
}
