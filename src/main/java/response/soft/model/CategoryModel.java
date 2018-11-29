package response.soft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import response.soft.core.BaseModel;

import java.util.UUID;

@Data
@EqualsAndHashCode
public class CategoryModel extends BaseModel {
    private UUID id;
    private String name;
    private String description;
}
