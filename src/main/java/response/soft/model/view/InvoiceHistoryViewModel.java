package response.soft.model.view;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode
public class InvoiceHistoryViewModel {
    private UUID invoiceId;
    private UUID customerId;
    private String customerName;
    private String invoiceNo;
    private Double invoiceAmount;
    private String invoiceStatus;

}
