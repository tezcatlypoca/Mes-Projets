path = require('path');
const express = require("express");
const cors = require("cors");
const http = require('http');
const socketio = require('socket.io')
const bodyParser = require("body-parser");

const app = express();
const server = http.createServer(app);
const io = socketio(server);

app.use(bodyParser.urlencoded({extended:true}));
//set static folder
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.json());
app.use(cors({ origin: true }));

io.on('connection', socket => {
  console.log('New connexion....');
  socket.emit('message', 'Welcome to Chat Coloc');
});


// GET routes
app.get('/chat', (req, res) => {
  res.sendFile(path.join(__dirname, '/public/chat.html'));
});

// POST routes
app.post('/enter', (req, res) => {
  const username = req.body.username;
  const room = req.body.room;
  res.redirect('/chat');
});

server.listen(3001);