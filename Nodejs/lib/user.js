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
            console.error(err);
            res.send('Failed to insert location information to db');
        } else {
            console.log(result);
            res.send('ok')
        }
    });
};