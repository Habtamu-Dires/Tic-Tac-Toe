import React, { useEffect, useState } from "react";
import SockJS from 'sockjs-client';
import SingUp from "./SignUp";
import Login from "./Login";
import ChatRoom from "./ChattRoom";
import { useNavigate, useLocation } from 'react-router-dom';

function Home() {

    const[authUser, setAuthUser] = useState(null);
    const history = useNavigate();
    const location = useLocation();
    
    console.log("The user " + authUser)
    // console.log("token " + authUser.token);

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        if (params.get('oauth2')) {
           
            if(params.get('token')){
                console.log("The username is " + params.get('username'));
                console.log("The token is " + params.get('token'));
                setAuthUser({username:params.get('username'),token:params.get('token')})
            }
           
        } else {
            // Handle regular authentication
            history('login'); // Redirect to the login page
        }
    }, [history, location.search]);

    return (
        <div>
            {(authUser === null)&& 
               <Login setAuthUser={setAuthUser}/> 
            }
            {authUser !== null && <ChatRoom setAuthUser={setAuthUser} authUser={authUser}/>}
        </div>
    )
}

export default Home;