const express = require('express');         //  for app
const login = require('./lib/login')        //  for login
const register = require('./lib/register')  //  for register
const parent = require('./lib/parent')      //  for parent
const user = require('./lib/user')          //  for user

var app = express()
app.use(express.urlencoded({ extended: false }))
app.use(express.json());

/* login page */
app.post('/login', (req, res) => { login.signIn(req, res) });
/* register page */
app.post('/check/id', function (req, res) { register.checkId(req, res) });
app.post('/register', function (req, res) { register.signUp(req, res) });
/* user mode */
app.post('/user', function (req, res) { user.send(req, res); });
/* parent mode */
app.post('/parent/setDestination', function(req, res) { parent.setDestination(req, res)});
app.post('/parent/reqLoc', function(req, res) { parent.reqLoc(req, res) });
app.post('/parent/register', function (req, res) { parent.registUser(req, res) });
app.post('/parent/delete', function (req, res) { parent.deleteUser(req, res) });
app.post('/parent/edit', function (req, res) { parent.editInfo(req, res) });
app.post('/parent', function (req, res) { parent.main(req, res) });
/* listen... */
app.listen(5555, function () { console.log('I Stick Server is listening on port 5555') })