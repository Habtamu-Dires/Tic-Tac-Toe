package com.example.server.user;

import com.example.server.authority.Authority;
import com.example.server.token.Token;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_user")
public class User {

    @Id
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private boolean onlineStatus;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "username")},
            inverseJoinColumns = {@JoinColumn(name = "authority_id", referencedColumnName = "id")})
    private List<Authority> authorities;
    @OneToMany(mappedBy = "user")
    private List<Token> tokenList;

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", onlineStatus=" + onlineStatus +
                ", authorities=" + authorities +
                '}';
    }
}
