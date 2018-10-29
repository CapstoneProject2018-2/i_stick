var mysql = require('mysql')
module.exports = conn
var conn = mysql.createConnection({
  host: '',
  user: '',
  password: '',
  database: ''
});
conn.connect(); //  database 접속