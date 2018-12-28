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
public class ProductSalesModel extends BaseModel {
    private UUID id;
    private UUID storeOutId;
    private UUID productId;
    private UUID customerId;
    private Double buyPrice;
    private Integer salesType;
    private Double salesPrice;
    private Date date;
    private Integer supportPeriodInMonth;
}
