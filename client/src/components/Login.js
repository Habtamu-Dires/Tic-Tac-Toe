import React, { useState, useEffect } from "react";
import SingUp from "./SignUp";

function Login(props){
   const [user, setUser] = useState(null);
   const[type, setType] = useState("Login");    

   const login = () => {      
   fetch("http://localhost:8080/api/v1/auth/login",{
      method: "POST",
      body: JSON.stringify(user),
      credentials: "same-origin", 
      headers: {
         "Content-Type": "application/json",
      },
   })
   .then(res =>res.json())
   .then(data => {
      console.log(data)
      console.log("The data.token === " + data.token)
      if(data.token !== undefined){
         const {token,username} = data;
         props.setAuthUser({token, username});
      }         
   })
   .catch(err => console.log('error ' + err))      
   }
   
   const handleClick = ({provider}) => {
      console.log("provider" + provider);
      window.location.href = `http://localhost:8080/oauth2/authorization/${provider}`;
   }


    return(
      <div>
         {type === "Login" && <div className="signUp">
         <label style={{fontSize: '2em', fontWeight: 'bold'}}>Login</label>
         <input placeholder="UserName" onChange={(event) => {
            setUser({...user, "username": event.target.value})
         }}/>  

         <input placeholder="Password" 
            onChange={(event) => {
               setUser({...user, "password": event.target.value})
         }}/>  
        
        <button onClick={login}>Login</button>

        <button type="button" onClick={()=>handleClick({provider:'github'})}>GitHub</button>
        <button type="button" onClick={()=>handleClick({provider:'GOOGLE'})}>Google</button><br/>
        <a href="#" onClick={() =>setType("Register")}>Register</a>
      </div>}
      {type === "Register" && 
         <SingUp setAuthUser={props.setAuthUser}/>
      }  
      </div>
      
    );
}

export default Login;