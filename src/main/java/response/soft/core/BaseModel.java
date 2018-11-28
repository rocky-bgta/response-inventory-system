/**
 * Created By: Md. Nazmus Salahin
 * Created Date: 29-Dec-17
 * Time: 3:20 PM
 * Modified By:
 * Modified date:
 * (C) CopyRight Nybsys ltd.
 */
package response.soft.core;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public abstract class BaseModel implements Serializable {

    private String createdBy;
    private String updatedBy;
    private Date createdDate;
    private Date updatedDate;
    private Integer status;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseModel)) return false;
        BaseModel baseModel = (BaseModel) o;
        return Objects.equals(getCreatedBy(), baseModel.getCreatedBy()) &&
                Objects.equals(getUpdatedBy(), baseModel.getUpdatedBy()) &&
                Objects.equals(getCreatedDate(), baseModel.getCreatedDate()) &&
                Objects.equals(getUpdatedDate(), baseModel.getUpdatedDate()) &&
                Objects.equals(getStatus(), baseModel.getStatus());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getCreatedBy(), getUpdatedBy(), getCreatedDate(), getUpdatedDate(), getStatus());
    }

    @Override
    public String toString() {
        return "BaseModel{" +
                "createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", status=" + status +
                '}';
    }
}
