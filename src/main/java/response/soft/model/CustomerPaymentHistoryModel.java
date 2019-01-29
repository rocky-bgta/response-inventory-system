package response.soft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import response.soft.core.BaseModel;

import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode
public class CustomerPaymentHistoryModel extends BaseModel {
    private UUID id;
    private String invoiceNo;
    private UUID customerId;
    private Double paidAmount;
    private Date paymentDate;
}
