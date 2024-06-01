import React from "react";

function ButtonGroup(props){
   
    const handleButtonClick = (buttonName) => {
      switch (buttonName) {
        case 'Chat':
          props.showChat();
          break;
        case 'Play':
          props.playGame();
          break;
        case 'History':
          props.showHistory();
          break;
        default:
          break;
      }
    };
  
    return (
      <div>
        <button
          type='button'
          className={props.activeButton === 'Chat' ? 'joinGame active' : 'joinGame'}
          onClick={() => handleButtonClick('Chat')}
        >
          Chat
        </button>
  
        <button
          type='button'
          className={props.activeButton === 'Play' ? 'joinGame active' : 'joinGame'}
          onClick={() => handleButtonClick('Play')}
        >
          Play
        </button>
  
        <button
          type='button'
          className={props.activeButton === 'History' ? 'joinGame active' : 'joinGame'}
          onClick={() => handleButtonClick('History')}
        >
          History
        </button>
      </div>
    );
}

export default ButtonGroup;