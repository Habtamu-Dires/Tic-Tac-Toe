package com.example.server.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
//    @Query("SELECT u FROM User u WHERE u.email =:email")
//    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username =:username")
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.onlineStatus=true")
    Optional<List<User>> findOnlineUsers();
}
