import React, {useState, useEffect} from 'react';
import SockJS from 'sockjs-client';
import {over} from 'stompjs';
import GameRoom from './GameRoom';
import History from './History';
import  ButtonGroup  from './ButtonGroup';

var stompClient =null;
const ChatRoom = (props) => {

    const[game, setGame] = useState({
        id: '',
        firstPlayerId: '',
        secondPlayerId: '',
        board: '',
        winner: '',
        tie: null,
        turn: '',
        counter: 0
    });

    const [privateChats, setPrivateChats] = useState(new Map());     
    const [onlineUsers, setOnlineUsers] = useState(new Map());   
    const [publicChats, setPublicChats] = useState([]); 
    const [tab,setTab] =useState("CHATROOM");
    const [mode, setMode] = useState("MESSAGE");
    const [userData, setUserData] = useState({
        username: props.authUser.username,
        receivername: '',
        connected: false,
        message: ''
      }); 
    useEffect(() => {
      //console.log(userData);
        connect()
    }, []);

    const connect =()=>{
        //const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhIiwiaWF0IjoxNzE0MTMyODIyLCJleHAiOjE3MTQxMzQyNjJ9.wx35UFIl1zg0zx0l0rXL2u4U9DaI3dUdUOmIVAm8JY0';
        let Sock = new SockJS(`http://localhost:8080/ws?token=${props.authUser.token}`);
        stompClient = over(Sock);
        let headers = {
            Authorization: "Bearer " + props.authUser.token
        };
        stompClient.connect(
            headers,
            onConnected, 
            onError
        );
    }

    const onConnected = () => {
        setUserData({...userData,"connected": true});
        stompClient.subscribe('/topic/public', onMessageReceived);
        stompClient.subscribe('/user/'+userData.username+'/my-messages', onPrivateMessage);
        stompClient.subscribe('/user/' + userData.username+'/game', onGamePlay);
        userJoin();
        fetchOnlineUsers();
    }

    const userJoin=()=>{
          var chatMessage = {
            sender: userData.username,
            status:"JOIN"
          };
          stompClient.send("/app/message", 
            {Authorization: "Bearer " + props.authUser.token}
          , JSON.stringify(chatMessage));
    }

    function fetchOnlineUsers() {
        fetch('http://localhost:8080/api/v1/users/online-users',{
            headers: {
                "Authorization": "Bearer " + props.authUser.token,
                "Content-Type": "application/json",
            },
            credentials: "same-origin"
        })
        .then(response => response.json())
        .then(data => {
            console.log("The data " + data);
            data.forEach(userMessage => {
                if(!onlineUsers.get(userMessage.sender) && userMessage.sender != userData.username){
                    onlineUsers.set(userMessage.sender,[]);
                    setOnlineUsers(new Map(onlineUsers));
                }
                if(!privateChats.get(userMessage.sender) && userMessage.sender != userData.username){
                    privateChats.set(userMessage.sender,[]);
                    setOnlineUsers(new Map(privateChats));
                }
            });
        })
    }

    const onMessageReceived = (payload)=>{
        var payloadData = JSON.parse(payload.body);
        console.log("message recived " + payloadData);
        switch(payloadData.status){
            case "JOIN":
                if(!onlineUsers.get(payloadData.sender) && payloadData.sender != userData.username){
                    onlineUsers.set(payloadData.sender,[]);
                    setOnlineUsers(new Map(onlineUsers));
                }
                if(!privateChats.get(payloadData.sender) && payloadData.sender != userData.username){
                    privateChats.set(payloadData.sender,[]);
                    setOnlineUsers(new Map(privateChats));
                }
                break;
            case "MESSAGE":
                publicChats.push(payloadData);
                setPublicChats([...publicChats]);
                break;
        }
    }
    
    const onPrivateMessage = (payload)=>{ 
        console.log(payload);
        var payloadData = JSON.parse(payload.body);
        if(privateChats.get(payloadData.sender)){
            privateChats.get(payloadData.sender).push(payloadData);
            setPrivateChats(new Map(privateChats));
        }else{
            let list =[];
            list.push(payloadData);
            privateChats.set(payloadData.sender,list);
            setPrivateChats(new Map(privateChats));
        }
    }

    const onGamePlay = (payload) => {
        var payloadData = JSON.parse(payload.body);
        
        setGame({...game, 
            'id': payloadData.id,
            'firstPlayerId': payloadData.firstPlayerId,
            'secondPlayerId': payloadData.secondPlayerId,
            'board': payloadData.board,
            'winner': payloadData.winner,
            'tie': payloadData.tie,
            'turn': payloadData.turn,
            'counter': payloadData.counter
        })
    }

    const onError = (err) => {
        console.log(err);
        
    }

    const handleMessage =(event)=>{
        const {value}=event.target;
        setUserData({...userData,"message": value});
    }
    const sendPublicMessage=()=>{
            if (stompClient) {
              var chatMessage = {
                sender: userData.username,
                recipient:"ALL",
                content: userData.message,
                type: "PUBLIC",
                status:"MESSAGE"
              };
              console.log(chatMessage);
              stompClient.send("/app/message",
               {Authorization: "Bearer " + props.authUser.token}
              , JSON.stringify(chatMessage));
              setUserData({...userData,"message": ""});
            }
    }

    const sendPrivateMessage=()=>{
        if (stompClient) {
          var chatMessage = {
            sender: userData.username,
            recipient:tab,
            content: userData.message,
            type: "PRIVATE",
            status:"MESSAGE"
          };
          
          if(userData.username !== tab){
            privateChats.get(tab).push(chatMessage);
            setPrivateChats(new Map(privateChats));
          }
          stompClient.send("/app/private-message",
          {Authorization: "Bearer " + props.authUser.token},
           JSON.stringify(chatMessage));
          setUserData({...userData,"message": ""});
        }
    }

    const handleTablClick = (username) => {
        setTab(username);
        fetchChatMessages(username);
        fetchOnGoingGame(username);
    }

    function fetchChatMessages(username){
        fetch(`http://localhost:8080/messages/${userData.username}/${username}`,{
            headers: {
                "Authorization": "Bearer " + props.authUser.token,
                "Content-Type": "application/json",
            },
            credentials: "same-origin"
        })
        .then(response => response.json())
        .then(dataList => {
            console.log(dataList)
            let senderList = [];
            if(dataList.length !== 0){
                dataList.forEach(data => {
                   
                    if(!senderList.includes(data.sender) && privateChats.get(data.sender)){    
                        privateChats.set(data.sender, []);            
                        setPrivateChats(new Map(privateChats));
                    }else if(!senderList.includes(data.sender)){
                        privateChats.set(data.sender,[]);
                        setPrivateChats(new Map(privateChats));
                    }
                    senderList.push(data.sender);
                });
                dataList.forEach(data => {
                    privateChats.get(username)
                        .push(data);
                    setPrivateChats(new Map(privateChats));
                });
            }

        })
        .catch(err => console.log(err));
    }

    //gmae
    const showChat = () => {
        setMode("MESSAGE");
    }
    const playGame = () => {
        setMode("GAME");
    }

    const showHistory =() => {
        setMode("HISTORY");
    }

    function fetchOnGoingGame(username) {
        fetch(`http://localhost:8080/game/${userData.username}/${username}`,{
            headers: {
                "Authorization": "Bearer " + props.authUser.token,
                "Content-Type": "application/json",
            },
            credentials: "same-origin"
        })
        .then(response => {
            console.log("The response of ongoing game")
            console.log(response)
            if(response.status === 200){
                return response.json();
            } else{
                throw new Error('Failed to fetch data. Response status is not 200.');
            }
           
        })
        .then(payloadData => {
            console.log("on goging game ")
            console.log(payloadData);

            if(payloadData.firstPlayerId != undefined){
                setGame({...game, 
                    'id': payloadData.id,
                    'firstPlayerId': payloadData.firstPlayerId,
                    'secondPlayerId': payloadData.secondPlayerId,
                    'board': payloadData.board,
                    'winner': payloadData.winner,
                    'tie': payloadData.tie,
                    'turn': payloadData.turn,
                    'counter': payloadData.counter
                })
            }
        }).catch(err => console.log(err));
    }
    
    // const handleusername=(event)=>{
    //     const {value}=event.target;
    //     setUserData({...userData,"username": value});
    // }
    // const registerUser=()=>{
    //     connect();
    // }

    const logout = () => {
        
        fetch('http://localhost:8080/api/v1/auth/logout', {
            method: 'post',
            headers: {
                "Authorization": "Bearer " + props.authUser.token,
                "Content-Type": "application/json",
            },
            credentials: "same-origin"
        })
        .then(response => {
            console.log("The log out resposne " + response)
            console.log(response.ok)
            console.log(props.authUser.token)
            if(response.ok){
                props.setAuthUser(null);
            }
        })
        .catch(err => console.log(err))
    }
    
    return (
    <div className="container">
        {userData.connected?
        <div className="chat-box">
            <div className="member-list">
                <div style={{height:'83%'}}>
                    <p>{userData.username}</p>
                    <ul>
                        <li onClick={()=>{setTab("CHATROOM")}} className={`member ${tab==="CHATROOM" && "active"}`}>Chatroom</li>
                        {[...onlineUsers.keys()].map((name,index)=>(
                            <li onClick={()=>handleTablClick(name)} className={`member ${tab===name && "active"}`} key={index}>
                                {name}
                            </li>
                            
                        ))}
                    </ul>
                </div>                
                <div>
                    <button type='button' onClick={logout}>Logout</button>
                </div>
            </div>
            
            {(tab==="CHATROOM" && mode==="MESSAGE") && <div className='chat-content'>
                <ul className="chat-messages">
                    {publicChats.map((chat,index)=>(
                        <li className={`message ${chat.sender === userData.username && "self"}`} key={index}>
                            {chat.sender !== userData.username && <div className="avatar">{chat.sender}</div>}
                            <div className="message-data">{chat.content}</div>
                            {chat.sender === userData.username && <div className="avatar self">{chat.sender}</div>}
                        </li>
                    ))}
                </ul>

                <div className="send-message">
                    <input type="text" className="input-message" placeholder="enter the message" value={userData.message} onChange={handleMessage} /> 
                    <button type="button" className="send-button" onClick={sendPublicMessage}>send</button>
                </div>
            </div>}
            {(tab!=="CHATROOM" && mode==="MESSAGE")&& <div className='chat-content'>
                <ButtonGroup showChat={showChat} playGame={playGame} showHistory={showHistory} 
                    activeButton = {'Chat'}
                />
                <ul className="chat-messages">
                    {[...privateChats.get(tab)].map((chat,index)=>(
                        <li className={`message ${chat.sender === userData.username && "self"}`} key={index}>
                            {chat.sender !== userData.username && <div className="avatar">{chat.sender}</div>}
                            <div className="message-data">{chat.content}</div>
                            {chat.sender === userData.username && <div className="avatar self">{chat.sender}</div>}
                        </li>
                    ))}
                </ul>

                <div className="send-message">
                    <input type="text" className="input-message" placeholder="enter the message" value={userData.message} onChange={handleMessage} /> 
                    <button type="button" className="send-button" onClick={sendPrivateMessage}>send</button>
                </div>
                
            </div>} 
            {mode === "GAME" && <div>
                    <ButtonGroup showChat={showChat} playGame={playGame} showHistory={showHistory}
                                activeButton = {'Play'}
                        />
                    <GameRoom setMode={setMode} 
                        stompClient={stompClient} 
                        username = {userData.username}
                        secondPlayer = {tab}
                        game={game} 
                        setGame={setGame}
                        token={props.authUser.token}
                    />
               
            </div>}  
            {mode === "HISTORY" && <div>
                <ButtonGroup showChat={showChat} playGame={playGame} showHistory={showHistory}
                        activeButton = {'History'}
                />
                <History username={userData.username} tab={tab}/> 
            </div>} 

        </div>
        :
        <div className="register">
            <p>Loading .... </p>
            <p>Please wait </p>
            {/* <input
                id="user-name"
                placeholder="Enter your name"
                name="username"
                value={userData.username}
                onChange={handleusername}
                margin="normal"
              />
              <button type="button" onClick={registerUser}>
                    connect
              </button>  */}
        </div>}
    </div>
    )
}

export default ChatRoom