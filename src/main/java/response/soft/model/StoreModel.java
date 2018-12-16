package response.soft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import response.soft.core.BaseModel;

import java.util.UUID;

@Data
@EqualsAndHashCode
public class StoreModel extends BaseModel {
    private UUID id;
    private String name;
    private String owner;
    private String phoneNo;
    private String address;
    private String email;
    private String comment;
}
