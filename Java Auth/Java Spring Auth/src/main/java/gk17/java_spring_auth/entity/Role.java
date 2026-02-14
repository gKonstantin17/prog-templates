package gk17.java_spring_auth.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Role", schema = "public", catalog = "auth-sharp")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Name")
    private String name;
}
