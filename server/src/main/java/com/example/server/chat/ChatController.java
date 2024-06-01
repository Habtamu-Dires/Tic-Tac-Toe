package com.example.server.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/private-message")
    public void processMessage(@Payload ChatNotification chatNotification) {
        ChatMessage savedMsg = chatMessageService.save(chatNotification);
//         send message to specific user's session, (for private messaging)
//         this will send to specific topic /user/recipientId/queue/messages'
        messagingTemplate.convertAndSendToUser(
                chatNotification.getRecipient(),
                "/my-messages",
                ChatNotification.builder()
                        .sender(savedMsg.getSender().getUsername())
                        .recipient(savedMsg.getRecipient().getUsername())
                        .type(savedMsg.getType())
                        .content(savedMsg.getContent())
                        .status("MESSAGE")
                        .build()
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatNotification>> findChatMessages(
            @PathVariable String senderId,
            @PathVariable String recipientId)
    {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderId, recipientId));
    }


    //public message
    @MessageMapping("public-message")
    public void publicMessage(@Payload ChatNotification chatNotification){
       // ChatMessage savedMsg = chatMessageService.save(chatNotification);
    }

}
/**
 * SimpleMessagingTemplate is a Spring framework class that provides methods for
   sending messages from the server to clients subscribed to a topic or queue.
 */

