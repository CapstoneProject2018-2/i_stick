var db = require('./db')
const bkfd2Password = require('pbkdf2-password'); //  for hash the passwd
var hasher = bkfd2Password();                     //  hash func

exports.signUp = function (req, res) {
  console.log('\n/register');
  //  register data from android and INSERT in Database
  const inputData = req.body;
  console.log(inputData);
  var sql = '';
  const id = inputData.id;
  const pw = inputData.pw;
  const name = inputData.name;
  const mobile = inputData.mobile;
  const token = inputData.token;
  if (inputData.type === 0) {  //  user
    sql = 'INSERT INTO user (id, pw, salt, name, mobile, token) VALUES (?,?,?,?,?,?);';
  } else if (inputData.type === 1) {  //  parent
    sql = 'INSERT INTO parent (id, pw, salt, name, mobile, token) VALUES (?,?,?,?,?,?);';
  }
  var opts = { password: pw };
  hasher(opts, function (err, pass, salt, hash) {
    db.query(sql, [id, hash, salt, name, mobile, token], function (err, data) {
      if (err) {
        console.log(err);
        res.send('에러로 인해 아이디 등록이 실패하였습니다. 잠시후 다시 시도해주세요.')
      } else {
        console.log(data[0]);
        res.send(inputData.id);
      }
    });
  });
}

exports.checkId = function (req, res) {
  console.log('\n/check/id');
  const inputData = req.body;
  const id = inputData.id;
  const type = inputData.type;
  

  var sql = '';
  if (type === 0) {  //  user
    sql = 'SELECT id FROM user WHERE id=?';
  } else if (type === 1) {  //  parent
    sql = 'SELECT id FROM parent WHERE id=?';
  }
  db.query(sql, id, function (err, data) {
    if (err) {
      console.error(err);
      res.send('에러로 인해 아이디 검사가 실패하였습니다. 잠시 후 다시 시도해주세요.')
    } else if (data[0] === undefined) {
      console.log('일치하는 ID 없음.');
      res.send('ok');
    } else {
      console.log('존재하는 ID');
      res.send('이미 존재하는 아이디 입니다.');      
    }
  });
} // select * from 에서 *수정하기 (pw, salt 암호화가 되어야함)