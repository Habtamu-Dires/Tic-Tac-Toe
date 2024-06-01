package com.example.server.game;


import com.example.server.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "game")
public class Game {
    @Id
    @SequenceGenerator(
            name = "game_sequence",
            sequenceName = "game_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "game_sequence"
    )
    private Long id;
    private String chatId;
    @ManyToOne
    @JoinColumn(name = "firstPlayerId")
    private User firstPlayer;
    @ManyToOne
    @JoinColumn(name = "secondPlayerId")
    private User secondPlayer;
    private List<Character> board ;
    private String turn;
    private Integer counter;
    private String winner = null; // default
    private boolean tie = false;
    private Date timestamp;

    @Override
    public String toString() {
        return "Game{" +
                "id='" + id + '\'' +
                ", chatId='" + chatId + '\'' +
                ", firstPlayerId='" + firstPlayer.getUsername()+ '\'' +
                ", secondPlayerId='" + secondPlayer.getUsername() + '\'' +
                ", board=" + board +
                ", turn='" + turn + '\'' +
                ", counter=" + counter +
                ", winner='" + winner + '\'' +
                ", tie=" + tie +
                ", timestamp=" + timestamp +
                '}';
    }
}

/**
 * Winning ways {
 *
 * }
 */
