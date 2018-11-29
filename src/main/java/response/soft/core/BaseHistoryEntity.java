/**
 * Created By: Md. Nazmus Salahin
 * Created Date: 29-Jan-18
 * Time: 4:27 PM
 * Modified By:
 * Modified date:
 * (C) CopyRight Nybsys ltd.
 */
package response.soft.core;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;
import java.util.UUID;

@MappedSuperclass
@DynamicInsert
@DynamicUpdate
@Data
public abstract
class BaseHistoryEntity {


    private String messageId;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;


    private String entityClassPath;
    private String entityName;
    private Integer actionType;

    @Column(columnDefinition = "TEXT")
    private String jsonObject;

    private Date DateTime;
}
