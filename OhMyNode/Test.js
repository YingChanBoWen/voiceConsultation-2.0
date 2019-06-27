//console.log("OhMyNode")
//console.log(__filename)



var http = require("http");
var fs = require("fs");
var url = require("url");

var data = fs.readFileSync("JBBKinfo.json")
data = data.toString();

http.createServer(
	
	function (request,response) {
		
		console.log(request.body)
		
		
		response.writeHead(200 ,{'Content-Type': 'application/json; charset=utf-8'});
		response.write(""+data);
		response.end("\n");
	}
	
	
).listen(60000);

console.log('Server running at http://127.0.0.1:60000/');
