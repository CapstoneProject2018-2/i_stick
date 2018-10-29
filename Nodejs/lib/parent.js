var db = require('./db')
const bkfd2Password = require('pbkdf2-password'); //  for hash the passwd
var hasher = bkfd2Password();                     //  hash func

/** /parent/regist get parameter : pno, userID, userPW
 * check userID existence if it exist, check pw correction.
 * if user regist yet, then insert into rpu table 
 * if not, send error messages
 */
exports.registUser = function(req, res) {
    console.log('/parent/register');
    const inputData = req.body; //  id, pw
    const pno = inputData.pno;
    const id = inputData.id;
    const pw = inputData.pw;
    /* id로 pw와 salt일치 여부 확인 */
    var sql = 'SELECT * FROM user WHERE id=?'; //  uno, pw(hash), salt
    db.query(sql, id, function(err, info) {
        if (err) {
        console.log(err);
        res.send(err)
        } else if (info[0] == null) {
        console.log('Wrong ID');
        res.send('존재하지 않는 사용자의 ID입니다.');
        } else {  //  id 존재 hasher로 pw비교
        var uno = info[0].no;
        hasher({password: pw, salt: info[0].salt}, function(err, pass, salt, hash) {
            if (err) {
            console.log(err);
            res.send(err);;
            } else if (info[0].pw == hash) {  //  add relation to rpu relation
            console.log('correct!');
            var sql = 'INSERT INTO rpu(pno, uno) VALUES(?,?)'
            db.query(sql, [pno, uno], function(err, results) {
                if (err) {
                // console.log('Error');
                console.log(err);
                res.send('이미 등록된 사용자 입니다.');
                } else {
                /* implement 1. when user is already registered... : error
                **           2. when registration succeed*/
                console.log(info[0]); //  select * from user
                res.send(info[0]);
                }
            });
            } else {
            console.log('Wrong password');
            res.send('비밀번호를 잘못 입력하셨습니다.');
            }
        });
        }
    });
};

// parent mode
// parent login -> client(parent)
// can request registing the user using user's id, pw
// ParentActivity에서 '내 정보 수정' 버튼 클릭시 이동, 비밀번호 수정
exports.editInfo = function(req, res) {
        /*  form : id, recent pw, new pw (비밀번호 확인은 android 책임)*/
    console.log('/parent/edit');
    const inputData = req.body; //  id, oldpw, newpw
    console.log(inputData); //  data check
    var id = inputData.id;
    var oldpw = inputData.oldpw;
    var newpw = inputData.newpw;
    /*  oldpw가 일치하면 UPDATE, 일치하지 않으면 send error msg */
    var sql = 'SELECT pw, salt FROM parent WHERE id=?'
    db.query(sql, inputData.id, function(err, data) {
        console.log(data);
        if (err) {
        console.log(err);
        res.send(err);
        } else {  //  id 는 무조건 존재한다. query 의 결과는 하나가 나옴
        var opts = { password: oldpw, salt: data[0].salt}
        hasher(opts, function(err, pass, salt, hash) {
            if (data[0].pw != hash) {  // 비밀번호 불일치
            console.log('Wrong Password');
            res.send('Wrong Password');
            } else {  //  일치 -> update
            hasher({ password: newpw}, function(err, pass, salt, hash) {
                // 새로운 hash(db pw) 와 salt값 갱신
                var sql = 'UPDATE parent SET pw=?, salt=? WHERE id=?'
                db.query(sql, [hash, salt, id], function(err, results) {
                if (err) {
                    console.log(err);
                    res.send('Error');
                } else {
                    console.log(results);
                    console.log('change completed!!');
                    res.send('OK');
                }
                })
            });
            }
        });
        }
    });
};

exports.main = function(req, res) {
    console.log('/parent');
    const inputData = req.body;  //  pno, id 받아오기
    console.log(inputData);
    //  ParnetActivity : getUserList(String pid) = 담당하는 user의 목록 불러오기
    var sql = 'SELECT * FROM rpu WHERE pno=?'
    db.query(sql, inputData.pno, function(err, results, fields) {
        if (err) {
            console.log(err);
            res.send(err);
        } else {  //  results : RowDataPacket형태
            var num = results.length;
            if (num === 0) {
            res.send('no registed user...');  //  맡고있는 user가 없을때
            } else {  //  있을 때
            var sql = 'SELECT * FROM user WHERE ';
            for (var i = 0; i < num; i++){
                sql = sql + 'no=\'' + results[i].uno + '\'';
                if (i+1 != num)
                sql = sql + ' or ';
            }
            console.log(sql);
            db.query(sql, function(err, userInfo, fields) {
                if (err) {
                console.log(err);
                res.send(err);
                } else {
                console.log(userInfo);  //  JSONArray
                res.send(userInfo);
                }
            });
            }
        }
    });
};