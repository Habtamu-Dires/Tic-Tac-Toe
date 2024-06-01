package com.example.server.game;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/start-game")
    public void playGame(@Payload GameNotification notification){

        Game savedGame = gameService.saveGame(notification);
        GameNotification gameNotification = GameNotification.builder()
                .id(savedGame.getId())
                .firstPlayerId(savedGame.getFirstPlayer().getUsername())
                .secondPlayerId(savedGame.getSecondPlayer().getUsername())
                .turn(savedGame.getTurn())
                .board(savedGame.getBoard())
                .winner(savedGame.getWinner())
                .tie(savedGame.isTie())
                .counter(savedGame.getCounter())
                .build();

        messagingTemplate.convertAndSendToUser(
                savedGame.getSecondPlayer().getUsername(),
                "/game",
                gameNotification
        );

        messagingTemplate.convertAndSendToUser(
                savedGame.getFirstPlayer().getUsername(),
                "/game",
                gameNotification
        );
    }

    @MessageMapping("/play")
    public void update(@Payload GameNotification notification){
        Game existedGame = gameService.updateGame(notification);
        GameNotification gameNotification = GameNotification.builder()
                .id(existedGame.getId())
                .firstPlayerId(existedGame.getFirstPlayer().getUsername())
                .secondPlayerId(existedGame.getSecondPlayer().getUsername())
                .turn(existedGame.getTurn())
                .board(existedGame.getBoard())
                .winner(existedGame.getWinner())
                .tie(existedGame.isTie())
                .counter(existedGame.getCounter())
                .build();

        messagingTemplate.convertAndSendToUser(
                gameNotification.getTurn(),
                "/game",
                gameNotification
        );
    }

    @GetMapping("/game/{firstPlayerId}/{secondPlayerId}")
    public ResponseEntity<GameNotification> findAGame(
            @PathVariable("firstPlayerId") String firstPlayerId,
            @PathVariable("secondPlayerId") String  secondPlayerId
    ){
       var gameNotification = gameService.findAGame(firstPlayerId, secondPlayerId);
        if(gameNotification == null){
            return ResponseEntity.status(HttpStatusCode.valueOf(204))
                    .body(null);
        }
        return ResponseEntity.ok(gameNotification);
    }

    @GetMapping("/games/{firstPlayerId}/{secondPlayerId}")
    public ResponseEntity<List<GameNotification>> gameHistory(
            @PathVariable("firstPlayerId") String firstPlayerId,
            @PathVariable("secondPlayerId") String  secondPlayerId
    ){
        var response = gameService.findHistory(firstPlayerId, secondPlayerId);
        if(response.isEmpty()){
            return ResponseEntity.status(204)
                    .body(response);
        }
        return ResponseEntity.ok(response);
    }


    @GetMapping("/check-win/{board}/{ch}")
    public boolean checkWin(
            @PathVariable("board") List<Character> board,
            @PathVariable("ch") char ch
    ) {
        for(int i=0; i<board.size(); i++){
            if(board.get(i) == null){
                board.set(i, '-');
            }
        }
        return gameService.checkWin(board,ch);

    }
}
