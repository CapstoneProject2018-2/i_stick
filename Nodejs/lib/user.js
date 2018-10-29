var db = require('./db')

/** UserActivity main page
 * get location from mobile periodicaly
 * save location to db
 */
exports.send = function(req, res) {
    console.log('/user');
    const inputData = req.body; /*  id, latitude, longitude */
    var id = inputData.id;
    var longitude = inputData.longitude;
    var latitude = inputData.latitude;
    var sql = 'INSERT INTO rpu (uno, longitude, latitude) VALUES(?,?,?)'
    db.query(sql, [id, longitude, latitude], function(err, result) {
        if (err) {
            console.log(err);
            res.send('Failed to insert location information to db');
        } else {
            console.log(result);
            res.send('ok');
        }
    });
};