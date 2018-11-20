var db = require('./db')
const bkfd2Password = require('pbkdf2-password'); //  for hash the passwd
var hasher = bkfd2Password();                     //  hash func
//   from login page
// {id,pw,type}
exports.signIn = function (req, res) {
    console.log('/login');
    const inputData = req.body;
    console.log(inputData);
    const type = inputData.type;
    const token = inputData.token;

    var sql = '';
    if (type === 0) {  //  user
        sql = 'SELECT * FROM user WHERE id=?';
    } else if (type === 1) {  //  parent
        sql = 'SELECT * FROM parent WHERE id=?';
    }
    console.log(sql);
    db.query(sql, inputData.id, function (err, datas, fields) {
        console.log(datas);
        if (err) {
            console.log('Error!');
            res.send('에러로 인하여 로그인에 실패하였습니다. 잠시후 다시 시도해주세요.');
        } else if (datas[0] == null) {
            console.log('unknown ID');
            res.send('등록되지 않은 아이디 입니다.');
        } else {
            var opts = { password: inputData.pw, salt: datas[0].salt }  //  login pw, db salt
            hasher(opts, function (err, pass, salt, hash) {
                if (hash == datas[0].pw) {
                    var info = {
                        no: datas[0].no,
                        id: datas[0].id
                    }
                    res.send(info);
                    //  log in information is matching... update token data
                    updateToken(datas[0].no, type, token)
                } else {
                    console.log('wrong Password');
                    res.send('비밀번호가 일치하지 않습니다.');
                }
            });
        }
    });
}

function updateToken(no, type, token) {
    var sql = '';
    if (type === 0) {
        sql = 'UPDATE user SET token=? WHERE no=?'
    } else {
        sql = 'UPDATE parent SET token=? WHERE no=?'
    }
    db.query(sql, [token, no], function (err, result) {
        if (err) {
            console.log(err);
        } else {
            console.log('token is successfully updated');
        }
    })
}