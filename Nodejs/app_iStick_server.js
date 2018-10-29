const express = require('express');               //  for app

const login = require('./lib/login')        //  for login
const register = require('./lib/register')  //  for register
const parent = require('./lib/parent')      //  for parent
const user = require('./lib/user')          //  for user

var app = express()
app.use(express.urlencoded({ extended: false }))
app.use(express.json());

app.post('/login', (req, res) => {
  login.signIn(req, res);
});
app.post('/check/id', function(req, res) {  //  id, type
  register.checkId(req, res);
});
app.post('/register', function(req, res) {  //  id pw name mobile type
  register.signUp(req, res);
});

app.post('/user', function(req, res) {
  user.send(req, res);
});

app.post('/parent/register', function(req, res) { //  pno id pw 받아와 인증 후 등록
  parent.registUser(req, res);
}); //  button "추가" : regist user
app.post('/parent/edit', function(req, res) {
  parent.editInfo(req, res);
}); //  내 정보 수정
app.post('/parent', function(req, res) {  //
  parent.main(req, res);
});

app.listen(5555, function() {
  console.log('I Stick Server is listening on port 5555');
})