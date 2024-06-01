package com.example.server.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameNotification {
    private Long id;
    private String firstPlayerId;
    private String secondPlayerId;
    private List<Character> board;
    private String winner;
    private boolean tie;
    private String turn;
    private Integer counter;
    private Date timestamp;
}
