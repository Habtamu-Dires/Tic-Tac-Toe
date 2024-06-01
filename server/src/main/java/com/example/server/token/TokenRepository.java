package com.example.server.token;

import com.example.server.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query(
            """
            SELECT t FROM Token t WHERE t.user.username =:username
            AND (t.expired = false OR t.revoked = false)
            """
    )
   List<Token> findAllValidTokenByUser(String username);

    @Query("SELECT t FROM Token t WHERE t.token=:token")
   Optional<Token> findByToken(String token);

    @Query("SELECT t.user FROM Token t WHERE t.id=:id")
   Optional<User> findUserById(Integer id);
}
