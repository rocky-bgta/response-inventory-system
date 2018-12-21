package response.soft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import response.soft.core.BaseModel;

import java.util.UUID;

@Data
@EqualsAndHashCode
public class CustomerModel extends BaseModel {
    private UUID id;
    //private String customerCode;
    private String name;
    private String phoneNo1;
    private String phoneNo2;
    private String address;
    private String email;
    private Integer activity;
    private String comment;
    private String image;
}
