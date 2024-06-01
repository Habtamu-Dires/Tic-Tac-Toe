import React,{useState} from "react";
import Board from "./GameBoard";
import { Hidden } from "@material-ui/core";
import Footer from "./ButtonGroup";

function GameRoom(props){
    const[showNewGameBtn, setShowNewGameBtn] = useState(false);

    var game = '';
  
    if(props.game.firstPlayerId === ''){
      generateNewGame();
    }  else{
        
        if(props.game.winner !== '' && !showNewGameBtn){
            setShowNewGameBtn(true);
            alert(props.game.winner + " has won the game!!!");
           
        } else if(props.game.tie && !showNewGameBtn){
            setShowNewGameBtn(true);
            alert("The Game is tie");
        }

        if(props.game.winner === '' && !props.game.tie && showNewGameBtn){
            setShowNewGameBtn(false);
        }

        game = {
            'id': props.game.id,
            'firstPlayerId': props.game.firstPlayerId,
            'secondPlayerId': props.game.secondPlayerId,
            'board': props.game.board,
            'winner': props.game.winner,
            'tie': props.game.tie,
            'turn': props.game.turn,
            'counter': props.game.counter
        };

        console.log("props.gmae.id ====> " + props.game.id);

    }

   function generateNewGame() {
    if(showNewGameBtn) setShowNewGameBtn(false);
    const array = [' ',' ',' ', ' ',' ',' ', ' ',' ',' '];
       game = {
            firstPlayerId: props.username,
            secondPlayerId: props.secondPlayer,
            board: array,
            winner: '',
            tie: false,
            turn: '',
            counter: 0
        };
        props.stompClient.send("/app/start-game", 
        {Authorization: "Bearer " + props.token}, 
        JSON.stringify(game));

        props.setGame(game);
   }
   

    const newGame = () => {
        generateNewGame();
    }

    return(
        <div className="gameContainer">
            <Board  username={props.username} 
                    secondPlayer={props.secondPlayer} 
                    game={game}
                    stompClient = {props.stompClient}
                    setGame={props.setGame}
                    setShowNewGameBtn={setShowNewGameBtn}
                    showNewGameBtn={showNewGameBtn}
                    token={props.token}
            />
            
            {showNewGameBtn && 
            <button type='button' className="joinGame" onClick={newGame}>New Game</button>  
            }     
        </div>
    )
}

export default GameRoom;