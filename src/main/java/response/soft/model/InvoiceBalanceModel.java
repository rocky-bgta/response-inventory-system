package response.soft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import response.soft.core.BaseModel;

import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode
public class InvoiceBalanceModel extends BaseModel {
    private UUID id;
    private String invoiceNo;
    private Double paidAmount;
    private Double dueAmount;
    private Double grandTotal;
    private Date date;
}
