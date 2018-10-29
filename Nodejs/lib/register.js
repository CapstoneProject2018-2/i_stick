var db = require('./db')
const bkfd2Password = require('pbkdf2-password'); //  for hash the passwd
var hasher = bkfd2Password();                     //  hash func
exports.signUp = function(req, res) {
    console.log('/register');
    //  register data from android and INSERT in Database
    const inputData = req.body;
    console.log(inputData);
    var sql = '';
    var id = inputData.id;
    var pw = inputData.pw;
    var name = inputData.name;
    var mobile = inputData.mobile;
  
    if (inputData.type === 0) {  //  user
      sql = 'INSERT INTO user (id, pw, salt, name, mobile) VALUES (?,?,?,?,?);';
    } else if (inputData.type === 1) {  //  parent
      sql = 'INSERT INTO parent (id, pw, salt, name, mobile) VALUES (?,?,?,?,?);';
    }
    var opts = { password : pw };
    hasher(opts, function(err, pass, salt, hash) {
      db.query(sql, [id, hash, salt, name, mobile], function(err, data) {
        if (err) {
          console.log(err);
          res.send('failed the ID creation...')
        } else {
          console.log(data[0]);
          res.send(inputData.id);
        }
      });
    });
}

exports.checkId = function(req, res) {
    console.log('/check/id');
    const inputData = req.body;
     console.log(inputData);
     var sql = '';
    if (inputData.type === 0) {  //  user
        sql = 'SELECT * FROM user WHERE id=?';
    } else if (inputData.type === 1) {  //  parent
        sql = 'SELECT * FROM parent WHERE id=?';
    }
    db.query(sql, inputData.id, function(err, data) {
        if (err) {
        console.log(err);
        } else {
        console.log(data[0]);
        res.send(data[0]);
        }
    });
}