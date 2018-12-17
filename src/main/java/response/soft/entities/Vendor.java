package response.soft.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import response.soft.core.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode
@DynamicInsert
@DynamicUpdate
@Table(name = "vendor",uniqueConstraints=
@UniqueConstraint(columnNames={"name", "status"}))
public class Vendor extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "phone_no")
    @NotNull
    private String phoneNo;

    @Column(name = "bank_ac_no")
    private String bankAccountNo;

    @Column(name = "email")
    @Email
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "description")
    private String description;
}
