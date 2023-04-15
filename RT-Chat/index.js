path = require('path');
const express = require("express");
const cors = require("cors");
const http = require('http');
const socketio = require('socket.io')
const bodyParser = require("body-parser");
const formatMessage = require('./utils/messages');
const { userJoin, getCurrentUser, userLeave, getRoomUsers } = require('./utils/users');

const app = express();
const server = http.createServer(app);
const io = socketio(server);

const botName = 'BillyBot';

app.use(bodyParser.urlencoded({extended:true}));
//set static folder
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.json());
app.use(cors({ origin: true }));

io.on('connection', socket => {
  socket.on('joinRoom', ({username, room}) => {
    
    const user = userJoin(socket.id, username, room);
    socket.join(user.room);

    //welcome to current user
    socket.emit('message', formatMessage(botName, 'Welcome to Chat Coloc'));
  
    //broadcast when a user connects
    socket.broadcast.to(user.room).emit('message', formatMessage(botName, `${user.username} has joined the chat.`));
  
    // send users and room info
    io.to(user.room).emit('roomusers', {room: user.room, users: getRoomUsers(user.room)})
  });

  // Listen for a chat message
  socket.on('chatMessage', (msg) => {
    const user = getCurrentUser(socket.id);
    io.to(user.room).emit('message', formatMessage(user.username, msg));
  });

  //broadcast when a user disconnects
  socket.on('disconnect', () => {
    const user = userLeave(socket.id);

    if(user){
      io.to(user.room).emit('message', formatMessage(botName, `${user.username} has left the chat.`));
    }

    // send users and room info
    io.to(user.room).emit('roomusers', {room: user.room, users: getRoomUsers(user.room)})

  });
});

server.listen(3001);