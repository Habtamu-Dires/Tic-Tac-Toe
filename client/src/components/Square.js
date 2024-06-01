import React from "react";

function Square({choseSquare, val}){
    return(
        <div className="square" onClick={choseSquare}>
            {val}
        </div>
    );
}

export default Square;