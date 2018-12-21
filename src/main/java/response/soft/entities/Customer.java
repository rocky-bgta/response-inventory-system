package response.soft.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import response.soft.core.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode
@DynamicInsert
@DynamicUpdate
@Table(name = "customer")
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "customer_code",unique = true)
    @NotNull
    private String customerCode;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "phone_no1")
    @NotNull
    private String phoneNo1;

    @Column(name = "phone_no2")
    @NotNull
    private String phoneNo2;

    @Column(name = "address")
    private String address;

    @Column(name = "activity")
    private Integer activity;

    @Column(name = "comment")
    private String comment;

    @Column(name = "image")
    @Type(type="text")
    private String image;
}
