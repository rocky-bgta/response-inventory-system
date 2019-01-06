package response.soft.model.view;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode
public class ProductSalesReportViewModel {
    private Date fromDate;
    private Date toDate;
}
