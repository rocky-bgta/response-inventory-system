
package response.soft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import response.soft.core.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class FileUploadModel extends BaseEntity {
    private UUID id;
    private String name;
    private String type;
    private String base64StringFile;
    private Integer sizeInKb;
}

