/**
 * Created By: Md. Nazmus Salahin
 * Created Date: 29-Dec-17
 * Time: 3:20 PM
 * Modified By:
 * Modified date:
 * (C) CopyRight.
 */
package response.soft.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode
public abstract class BaseModel implements Serializable {


   // private static final long serialVersionUID = 1L;

    @JsonIgnore
    private String createdBy;
    @JsonIgnore
    private String updatedBy;
    @JsonIgnore
    private Date createdDate;
    @JsonIgnore
    private Date updatedDate;
    @JsonIgnore
    private Integer status;
}
