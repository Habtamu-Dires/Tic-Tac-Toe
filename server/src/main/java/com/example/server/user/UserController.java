package com.example.server.user;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDTO> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/online-users")
    public List<UserMessage> getOnlineUsers(){
       return userService.findOnlineUsers();
    }

    //chat
    @MessageMapping("/message")
    @SendTo("/topic/public")
    public UserMessage receiveMessage(@Payload UserMessage message){
        userService.setOnlineStatus(message.sender(), true);
        return message;
    }
}


