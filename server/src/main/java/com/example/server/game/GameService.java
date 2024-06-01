package com.example.server.game;


import com.example.server.chatroom.ChatRoomService;
import com.example.server.user.User;
import com.example.server.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final ChatRoomService chatRoomService;
    private final UserService userService;

    public Game saveGame(GameNotification gameNotification){
        var chatId = chatRoomService
                .getChatRoomId(gameNotification.getFirstPlayerId()
                        , gameNotification.getSecondPlayerId(),
                        true)
                .orElseThrow();

        User firstPlayer = userService.findUserByUserName(
                gameNotification.getFirstPlayerId());
        User secondPlayer = userService.findUserByUserName(
                gameNotification.getSecondPlayerId());

       Game game = Game.builder()
                .firstPlayer(firstPlayer)
                .secondPlayer(secondPlayer)
                .board(gameNotification.getBoard())
                .winner(gameNotification.getWinner())
                .tie(gameNotification.isTie())
                .counter(gameNotification.getCounter())
                .timestamp(new Date())
                .build();

        game.setChatId(chatId);

        gameRepository.save(game);
        return game;
    }

    public Game updateGame(GameNotification gameNotification){
        Game existedGame = gameRepository.findById(gameNotification.getId())
                .orElse(null);
        if(existedGame != null){
            existedGame.setBoard(gameNotification.getBoard());
            existedGame.setWinner(gameNotification.getWinner());
            existedGame.setTie(gameNotification.isTie());
            existedGame.setTurn(gameNotification.getTurn());
            existedGame.setCounter(gameNotification.getCounter());

            gameRepository.save(existedGame);
        } else {
            System.out.println("hello hello the saved game is null " + gameNotification.getId());
        }

        return existedGame;
    }

    public boolean checkWin(List<Character> board, char ch){
        List<Character> tempList = new ArrayList<>(board);

        List<Integer> exitedPattern = tempList.stream()
                .filter(c -> c == ch)
                .map(c -> {
                    int index = tempList.indexOf(c);
                    tempList.set(index, '-');
                    return index;
                })
                .toList();

        System.out.println(exitedPattern);

        //check win
        return WinningPattern.patterns.stream()
                .anyMatch(exitedPattern::containsAll);
    }

    
    public GameNotification findAGame(String firstPlayerId, String secondPlayerId){
        var chatId = chatRoomService.getChatRoomId(firstPlayerId,secondPlayerId, false);
        List<Game> gameList =  chatId
                .map(gameRepository::findByChatId)
                .orElse(null);

        if(gameList != null) {
            Game ongoingGame = gameList.stream()
                    .filter(game -> game.getWinner().isEmpty() && !game.isTie())
                    .findFirst()
                    .orElse(null);

            if(ongoingGame != null){
                return GameNotification.builder()
                        .id(ongoingGame.getId())
                        .firstPlayerId(ongoingGame.getFirstPlayer().getUsername())
                        .secondPlayerId(ongoingGame.getSecondPlayer().getUsername())
                        .counter(ongoingGame.getCounter())
                        .winner(ongoingGame.getWinner())
                        .turn(ongoingGame.getTurn())
                        .board(ongoingGame.getBoard())
                        .build();
            }
        }
        return null;

    }

    public List<GameNotification> findHistory(String firstPlayerId, String secondPlayerId){
        var chatId = chatRoomService.getChatRoomId(firstPlayerId,
                                     secondPlayerId,
                false);

        List<Game> gameList =  chatId
                .map(gameRepository::findByChatId)
                .orElse(null);
        if(gameList != null) {
            List<GameNotification> gameNotifications = new ArrayList<>();
            gameList.forEach(game -> {
               GameNotification gameNotification =
                    GameNotification.builder()
                        .firstPlayerId(game.getFirstPlayer().getUsername())
                        .secondPlayerId(game.getSecondPlayer().getUsername())
                        .winner(game.getWinner())
                        .tie(game.isTie())
                        .timestamp(game.getTimestamp())
                        .build();
               gameNotifications.add(gameNotification);
            });

            return gameNotifications;
        } else {
            return List.of();
        }
    }

}
