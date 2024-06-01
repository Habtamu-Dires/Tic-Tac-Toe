import React, { useState, useEffect } from "react";
import Square from "./Square";

function Board(props){

    const[board, setBoard] = useState(props.game.board);
    
    useEffect(() => {
        setBoard(props.game.board);
      }, [props.game.board]
    );

    async function checkWin (board, char) {
        
        const response = await fetch(`http://localhost:8080/check-win/${board}/${char}`,{
            headers: {
                "Authorization": "Bearer " + props.token,
                "Content-Type": "application/json",
            },
            credentials: "same-origin"
        });
        const data = await response.json();
        return data;
    }

    async function choseSquare(square) {
        if((props.game.turn === props.username || props.game.turn === null )
                && board[square] === ' ' 
                && props.game.winner === '')
        {
            let tie = false;
            let winner = '';

            const updatedBoard = board.map((val, idx)=>{
                if(idx === square && val === ' '){
                   if(props.game.counter % 2 === 0) return 'X';
                   return 'O';
                }
                return val;
            });

            setBoard(updatedBoard);
            //check win
            if(props.game.counter >= 4){
                const winStatus = await checkWin(updatedBoard, updatedBoard[square]);
                if(winStatus){
                    winner = props.username;
                }
            } 

            //check tie
            if(props.game.counter >= 8){
                tie = true;
            }


            let counter = props.game.counter  + 1;
            
            const game = {
                'id': props.game.id,
                'firstPlayerId': props.game.firstPlayerId,
                'secondPlayerId': props.game.secondPlayerId,
                'board': updatedBoard,
                'winner': winner,
                'tie': tie,
                'turn': props.secondPlayer,
                'counter': counter
            };

            props.stompClient.send("/app/play", 
            {Authorization: "Bearer " + props.token},
             JSON.stringify(game));
            props.setGame(game);

            if(winner !== '' && !props.showNewGameBtn){
                props.setShowNewGameBtn(true);
                alert(props.username + " has won the gmae!!!");
            }
            if(tie === true && !props.showNewGameBtn){
                props.setShowNewGameBtn(true);
                alert("The Game is tie!!!");
              
            }
        }
    }


    return(
        <div className="board">
            <div className="row">
                <Square val={board[0]} 
                    choseSquare={() =>choseSquare(0)} 
                />
                <Square  val={board[1]}
                    choseSquare={() =>choseSquare(1)} 
                />
                <Square val={board[2]}
                     choseSquare={() =>choseSquare(2)} 
                />
            </div>
            <div className="row">
                <Square val={board[3]}
                    choseSquare={() =>choseSquare(3)} 
                />
                <Square val={board[4]}
                    choseSquare={() =>choseSquare(4)} 
                />
                <Square val={board[5]}
                    choseSquare={() =>choseSquare(5)} 
                />
            </div>
            <div className="row">
                <Square val={board[6]}
                    choseSquare={() =>choseSquare(6)} 
                />
                <Square val={board[7]}
                    choseSquare={() =>choseSquare(7)} 
                />
                <Square val={board[8]}
                    choseSquare={() =>choseSquare(8)} 
                />
            </div>
        </div>
    )
}

export default Board;