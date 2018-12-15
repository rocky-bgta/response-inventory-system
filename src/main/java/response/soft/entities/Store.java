package response.soft.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import response.soft.core.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode
@DynamicInsert
@DynamicUpdate
@Table(name = "store")
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "owner")
    @NotNull
    private String owner;

    @Column(name = "address")
    @NotNull
    private String address;

    @Column(name = "email")
    private String email;

    @Column(name = "description")
    private String description;
}
