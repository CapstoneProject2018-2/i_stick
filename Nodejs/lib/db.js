var mysql = require('mysql')
var db = mysql.createConnection({
   host: 'localhost',
  user: 'root',
  password: '111111',
  database: 'i_stick'
});
db.connect(); //  database 접속
module.exports = db;