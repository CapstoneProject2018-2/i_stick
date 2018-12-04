var db = require('./db')
const bkfd2Password = require('pbkdf2-password'); //  for hash the passwd
var hasher = bkfd2Password();                     //  hash func

//   from login page
// {id,pw,type}
exports.signIn = function (req, res) {
    console.log('\n/login');
    const inputData = req.body;
    const id = inputData.id;
    const pw = inputData.pw;
    const type = inputData.type;
    const token = inputData.token;
    console.log('User ID: ', id, ' type: ', type);

    var sql = '';
    if (type === 0) {  //  user
        sql = 'SELECT * FROM user WHERE id=?';
    } else if (type === 1) {  //  parent
        sql = 'SELECT * FROM parent WHERE id=?';
    }
    db.query(sql, id, function (err, datas) {
        // console.log(datas);
        if (err) {
            console.error('Error!');
            res.send('에러로 인하여 로그인에 실패하였습니다. 잠시후 다시 시도해주세요.');
        } else if (datas[0] == null) {  //  일치하는 ID를 찾지 못하였을 때
            console.log('unknown ID');
            res.send('등록되지 않은 아이디 입니다.');
        } else {    //  일치하는 ID를 찾았을 경우
            var opts = { password: pw, salt: datas[0].salt }  //  login pw, db salt
            hasher(opts, function (err, pass, salt, hash) {
                if (hash == datas[0].pw) {  //  confirm password
                    var info = {
                        no: datas[0].no,
                        id: datas[0].id,
                        name: datas[0].name,
                        mobile: datas[0].mobile
                    }
                    res.send(info); //  send client info
                    //  Log-in information matched. update token data
                    updateToken(datas[0].no, type, token)
                } else {    // password is not correct
                    console.log('wrong Password');
                    res.send('비밀번호가 일치하지 않습니다.');
                }
            });
        }
    });
}

/* update user token for FCM */
function updateToken(no, type, token) {
    var sql = '';
    if (type === 0) {
        sql = 'UPDATE user SET token=? WHERE no=?'
    } else {
        sql = 'UPDATE parent SET token=? WHERE no=?'
    }
    db.query(sql, [token, no], function (err, result) {
        if (err) {
            console.error(err);
        } else {
            console.log('token is successfully updated');
        }
    })
}