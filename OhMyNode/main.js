//console.log("OhMyNode")
//console.log(__filename)

/*
基本方法
var http = require("http");
var fs = require("fs");
var url = require("url");

var data = fs.readFileSync("input.txt")
data = data.toString();

http.createServer(
	
	function (request,response) {
		
		console.log(request.body)
		
		
		response.writeHead(200 ,{'Content-Type': 'application/json; charset=utf-8'});
		response.write(data);
		response.end("\n");
	}
	
	
).listen(8888);

console.log('Server running at http://127.0.0.1:8888/');
*/




//以下使用express框架
//[Node.js搭建Web服务器](https://www.jianshu.com/p/a05835de5853)

/*------------------------------------*/
//1.添加模块
var express = require('express')
var bodyParse = require('body-parser')
/*------------------------------------*/

let regex1 = new RegExp("/^    $")

/*------------------------------------*/
//2. 创建express服务器
var server = express()
/*------------------------------------*/



/*------------------------------------*/
//3. 访问服务器(get或者post)
server.get('/question', function (request, response, next) {
	
	console.log(request.query.question)
	console.log(request)
	var question = request.query.question
	//response.send(question)
	if(question == "肚子疼"){
		response.send("还有其他症状吗？")
		
	}
	else if(question == "呕吐"){
		response.send("还有其他症状吗？")
	}
	else if(question == "没有"){
		response.send("综合病症分析：阑尾炎")
	}
	else {
		response.send("语法错误")
	}
		
	next()
},function (request, response) {
	console.log('已发送')
	
	
})

//4. 生成解析器
//var urlencoded = bodyParse.urlencoded({ extends:true })
//var jsonParser = bodyParse.json() //application/json

//server.use('./question', jsonParser) //中间件: 把请求体参数 存放到request.body

server.post('/question', function (request, response, next) {
	
	
	console.log(request)
	next()
},function (request, response) {
	console.log('已发送')
})
/*------------------------------------*/


/*------------------------------------*/
//4. 绑定端口
var port = 60000
server.listen(port)
console.log('启动端口'+port)
/*------------------------------------*/



*/