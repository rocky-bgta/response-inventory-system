package response.soft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode
public class CategoryModel{
    private UUID id;
    private String name;
    private String description;
}
