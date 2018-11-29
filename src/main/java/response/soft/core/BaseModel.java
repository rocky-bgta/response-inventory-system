/**
 * Created By: Md. Nazmus Salahin
 * Created Date: 29-Dec-17
 * Time: 3:20 PM
 * Modified By:
 * Modified date:
 * (C) CopyRight.
 */
package response.soft.core;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode
public abstract class BaseModel implements Serializable {
    private String createdBy;
    private String updatedBy;
    private Date createdDate;
    private Date updatedDate;
    private Integer status;
}
