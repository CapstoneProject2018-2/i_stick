var db = require('./db')
const bkfd2Password = require('pbkdf2-password'); //  for hash the passwd
var hasher = bkfd2Password();                     //  hash func
//   from login page
// {id,pw,type}
exports.signIn = function(req, res) {
    console.log('/login');
    const inputData = req.body;
    console.log(inputData);
    var sql = '';
    if (inputData.type === 0) {  //  user
        sql = 'SELECT * FROM user WHERE id=?';
    } else if (inputData.type === 1) {  //  parent
        sql = 'SELECT * FROM parent WHERE id=?';
    }
    console.log(sql);
    db.query(sql, inputData.id, function(err, datas, fields) {
        console.log(datas);
        // console.log(fields);
        if (err) {
        console.log('Error!');
        res.send('Error!');
        } else if (datas[0] == null) {
        console.log('unknown ID');
        res.send('unknown ID');
        } //  Error or do not exist corresponding ID...
        var opts = {password : inputData.pw, salt : datas[0].salt}  //  login pw, db salt
        hasher(opts, function(err, pass, salt, hash) {
        if (hash == datas[0].pw) {
            var info = {
            no : datas[0].no,
            id : datas[0].id
            }
            res.send(info);
        } else {
            console.log('wrong Password');
            res.send('wrong Password');
        }
        });
    }); 
}