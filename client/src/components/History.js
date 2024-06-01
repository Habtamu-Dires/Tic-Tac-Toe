import React,{useState, useEffect} from "react";

function History(props){
    
    const [gameHistory, setGameHistory] = useState([]);

  useEffect(() => {
    async function fetchGameHistory() {
      try {
        const response = await fetch(`http://localhost:8080/games/${props.username}/${props.tab}`,{
          headers:{

          }
        });
        const data = await response.json();
        console.log(data);
        setGameHistory(data);
      } catch (error) {
        console.error('Error fetching game history:', error);
      }
    }

    fetchGameHistory();
  }, [props.username, props.tab]);

  return (
    <div>
      <h2>History</h2>

      <div className="game-table">
        <div className="game-row">
            <span>Winner</span>
            <span>Tie</span>
            <span>Timestamp</span>
        </div>
        {gameHistory.map((game, index) => (
          <div className="game-row"  key={index}>
            <span> {game.winner}</span>
            <span>{game.tie.toString()}</span>
            <span>{game.timestamp}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default History;