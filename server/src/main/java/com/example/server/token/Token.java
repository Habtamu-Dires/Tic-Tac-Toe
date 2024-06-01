package com.example.server.token;

import com.example.server.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Struct;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "token")
public class Token {

    @Id
    @SequenceGenerator(
            name = "token_generator",
            sequenceName = "token_generator",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "token_generator"
    )
    private Integer id;
    private String token;

    private boolean expired = false;
    private boolean revoked = false;

    @ManyToOne
    @JoinColumn(name = "username")
    private User user;
}
