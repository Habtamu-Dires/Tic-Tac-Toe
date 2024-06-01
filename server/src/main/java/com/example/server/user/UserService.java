package com.example.server.user;

import com.example.server.authority.Authority;
import com.example.server.authority.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        return UserMapper.userDTOS(userRepository.findAll());
    }

    public void setOnlineStatus(String username, boolean status){
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isPresent()){
           User user = optionalUser.get();
           user.setOnlineStatus(status);

           userRepository.save(user);
        }
    }

    public User findUserByUserName(String  username){
        return userRepository.findByUsername(username).orElse(null);
    }

    public List<UserMessage> findOnlineUsers() {
      var userList = userRepository.findOnlineUsers();
      if(userList.isPresent()){
          List<UserMessage> userMessageList = new ArrayList<>();
          userList.get().forEach(user ->{
             userMessageList.add(
                     UserMessage.builder()
                             .sender(user.getUsername())
                             .build()
             );
          });
          return userMessageList;
      }

      return List.of();
    }



//    public User findUserByEmail(String  email){
//        return userRepository.findByEmail(email).orElse(null);
//    }
}
