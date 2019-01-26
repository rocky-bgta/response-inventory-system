package response.soft.model.view;

import lombok.Data;

import java.util.UUID;

@Data
public class CustomerPreviousDueViewModel {
    private String customerName;
    private UUID customerId;
    private Double previousDue;
}
