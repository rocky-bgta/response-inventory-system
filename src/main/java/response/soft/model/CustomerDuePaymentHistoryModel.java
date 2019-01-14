package response.soft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import response.soft.core.BaseEntity;
import response.soft.core.BaseModel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode
public class CustomerDuePaymentHistoryModel extends BaseModel {
    private UUID id;
    private String invoiceNo;
    private Double paidAmount;
    private Date paymentDate;
}
