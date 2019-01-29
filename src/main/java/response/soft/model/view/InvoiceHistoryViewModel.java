
package response.soft.model.view;

import lombok.Data;
import lombok.EqualsAndHashCode;
import response.soft.core.DropDownSelectModel;
import response.soft.entities.view.InvoiceHistoryView;

import java.util.List;

@Data
@EqualsAndHashCode
public class InvoiceHistoryViewModel {
    private List<InvoiceHistoryView> invoiceHistoryViewList;
    private List<DropDownSelectModel> customerDropDownList;
}

