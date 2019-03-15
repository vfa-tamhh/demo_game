module.exports = function(req, res) {

  var NCMB = require('ncmb');
  var ncmb = new NCMB('9928f6457501fedd1b77ab27fdc3b4fd7dbb08e6d32c3e2eb5ecd6af095ae83d', '4055eb9c49787bfbdb1ca8faad4b693eb14682ec9024625527aa330fd265c0d4');
  var GameScore = ncmb.DataStore('GameScore');
  var name = req.body.name;
  
  if (name) {
  	GameScore.equalTo('name', name)
	  .order('score',true)
	         .fetchAll()
	         .then(function(results){
	         	res.json(results[0]);
	         })
	         .catch(function(err){
	         	res.json(err);
	         });
  } else {
  	res.send('Please insert userID');
  }
  
}