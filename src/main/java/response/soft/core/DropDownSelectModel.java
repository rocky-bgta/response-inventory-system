package response.soft.core;

import lombok.Data;

import java.util.UUID;
@Data
public class DropDownSelectModel {
    UUID id;
    String name;
    String groupBy;
}
