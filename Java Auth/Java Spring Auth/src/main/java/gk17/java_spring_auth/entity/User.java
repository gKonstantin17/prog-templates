package gk17.java_spring_auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
@Entity
@Table(name = "User", schema = "public", catalog = "auth-sharp")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Login")
    private String login;

    @Column(name = "Password")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RoleId")
    private Role role;

    @JsonIgnore
    @Column(name = "RefreshTokenHash")
    private String refreshTokenHash;

    @JsonIgnore
    @Column(name = "RefreshTokenExpiryDate")
    private Timestamp refreshTokenExpiryDate;
}
