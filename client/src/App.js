import { Route, Routes } from 'react-router-dom';
import './App.css';
import Home from './components/Home';
import { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';

import { Button, ListItem,Avatar, List, ListItemText, TextField, Typography } from '@material-ui/core';
import SingUp from './components/SignUp';
import ChatRoom from './components/ChattRoom';



function App() {
  
  return(
    <Home />
  );
  

}

export default App;


  // return (
  //   <div>
  //     <Routes>
  //       <Route path='/' element={<Home />}/>
  //     </Routes>
  //   </div>
  // );