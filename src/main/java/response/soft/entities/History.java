package response.soft.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import response.soft.core.BaseHistoryEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode
@DynamicInsert
@DynamicUpdate
@Table(name = "history")
public class History{

    @Column(name = "message_id")
    private String messageId;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", nullable = false)
    private UUID id;


    @Column(name = "entity_class_path")
    private String entityClassPath;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "action_type")
    private Integer actionType;

    @Column(name="json_object", columnDefinition = "TEXT")
    private String jsonObject;

    @Column(name = "date_time")
    private Date DateTime;

}
