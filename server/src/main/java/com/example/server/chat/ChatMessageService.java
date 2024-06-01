package com.example.server.chat;

import com.example.server.chatroom.ChatRoomService;
import com.example.server.user.User;
import com.example.server.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;
    private final UserService userService;

    public ChatMessage save(ChatNotification chatNotification) {

        User sender = userService.findUserByUserName(chatNotification.getSender());
        User recipient = userService.findUserByUserName(chatNotification.getRecipient());

        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sender)
                .recipient(recipient)
                .content(chatNotification.getContent())
                .type(chatNotification.getType())
                .timestamp(LocalDateTime.now())
                .build();

        var chatId = chatRoomService
                .getChatRoomId(chatMessage.getSender().getUsername(),
                        chatMessage.getRecipient().getUsername(),
                        true
                )
                .orElseThrow(); // You can create your own dedicated exception
        chatMessage.setChatId(chatId);
        repository.save(chatMessage);
        return chatMessage;
    }

    public List<ChatNotification> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        List<ChatMessage> chatMessages = chatId.map(repository::findByChatId)
                        .orElse(new ArrayList<>());

        List<ChatNotification> chatNotificationList = new ArrayList<>();
        chatMessages.forEach(chatMessage -> {
            chatNotificationList.add(
              ChatNotification.builder()
                      .sender(chatMessage.getSender().getUsername())
                      .recipient(chatMessage.getRecipient().getUsername())
                      .content(chatMessage.getContent())
                      .type(chatMessage.getType())
                      .build()
            );
        });

        return chatNotificationList;
    }
}
