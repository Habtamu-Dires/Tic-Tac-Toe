package com.example.server.chat;

import com.example.server.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @SequenceGenerator(
            name = "chatMessage_sequence",
            sequenceName = "chatMessage_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "chatMessage_sequence"
    )
    private Long id;
    private String chatId;
    @ManyToOne
    @JoinColumn(name = "sender")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "recipient")
    private User recipient;
    private String content;
    private String type;
    private LocalDateTime timestamp;
}

