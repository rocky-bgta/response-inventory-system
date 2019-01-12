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
public class CustomerPaymentModel extends BaseModel {
    private UUID id;
    private UUID customerId;
    private String invoiceNo;
    private Double paidAmount;
    private Double dueAmount;
    private Double grandTotal;
    private Integer paidStatus;
    private Date date;

    //========================
    private String customerName;
}
