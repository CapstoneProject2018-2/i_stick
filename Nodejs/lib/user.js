var db = require('./db')

/** UserActivity main page
 * get location from mobile periodicaly
 * save location to db */
exports.send = function (req, res) {
    console.log('/user');
    const inputData = req.body; /*  uno, latitude, longitude */
    var uno = inputData.uno;
    var longitude = inputData.longitude;
    var latitude = inputData.latitude;
    var sql = 'INSERT INTO user_location (uno, longitude, latitude) VALUES(?,?,?)'
    db.query(sql, [uno, longitude, latitude], function (err, result) {
        if (err) {
            console.log(err);
            res.send('Failed to insert location information to db');
        } else {
            console.log(result);
            //  check nav_hist and lastest registered time is in 1000*60 and FLAG=N
            //  send destination loc to user
            var sql = "SELECT * FROM nav_hist WHERE no=(SELECT max(no) FROM nav_hist WHERE uno=?)"
            db.query(sql, uno, function(err, data){
                if (err) {
                    console.log(err);
                    res.send('서버의 에러로 데이터베이스 참조에 실패 하였습니다.')
                } else if (data[0] == null) {
                    console.log('no destination information in DB');
                    res.send('설정된 목적지 정보가 존재하지 않습니다.');
                } else {
                    var type = data[0].type;
                    switch (type) {
                        case 'Y':
                            console.log('이미 보내진 목적지 정보');
                            res.send('ok');
                            break;
                        case 'N':
                            console.log('send data and update type to Y');
                            var sql = "UPDATE nav_hist SET type='Y' WHERE no=?"
                            db.query(sql, data[0].no, function(err, ret) {
                                if (err) {
                                    console.log(err);
                                    res.send('서버의 업데이트 실패');
                                } else {
                                    console.log(ret);
                                    var destination = {
                                        longitude: data[0].longitude,
                                        latitude: data[0].latitude
                                    };
                                    res.send(destination);
                                }
                            })
                            break;
                    }                   
                }
            })
        }
    });
};