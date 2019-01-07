package response.soft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import response.soft.core.BaseModel;

import java.util.UUID;

@Data
@EqualsAndHashCode
public class VendorModel extends BaseModel {
    private UUID id;
    private String name;
    private String phoneNo1;
    private String email;
    private String address;
    private String description;
    private String phoneNo2;
    private String bankAccountNo;
}
