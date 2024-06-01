import { Axios } from "axios";
import React, { useState } from "react";

function SingUp(props){
    const [user, setUser] = useState(null);

    const signUp = () =>{
        fetch("http://localhost:8080/api/v1/auth/signup",{
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
    return(
      <div className="signUp">
         <label style={{fontSize: '2em', fontWeight: 'bold'}}>Sign Up</label>
         <input 
            placeholder="Frist Name" 
            onChange={(event) => {
                setUser({...user, firstName: event.target.value})
            }}
         /> 

         <input
             placeholder="Last Name" 
             onChange={(event) => {
                setUser({...user, lastName: event.target.value})
             }}
         /> 

         <input 
            placeholder="username" 
            onChange={(event) => {
                setUser({...user, username: event.target.value})
            }}
         />  

         <input 
            placeholder="Password" 
            type="password"
            onChange={(event) => {
            setUser({...user, password: event.target.value})
         }}/>  
          <button onClick={()=> signUp()}>Sign Up</button>
      </div>  
    );
}

export default SingUp;