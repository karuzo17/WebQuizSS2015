document.addEventListener('DOMContentLoaded', initLogin, false);

var socket;
var bereitZumSenden = false;
var firstPlayer = false;
var tmpCat = false;
var animation;
var timer;

function initLogin() { // Listener registrieren fÃ¼r Buttons
	var login = window.document.getElementById("loginButton");
	login.addEventListener("click", send, false);

	var url = 'ws://localhost:8080/WebQuizSS15/Login';

	socket = new WebSocket(url);

	socket.onopen = sendenMoeglich;
	socket.onclose = Closing;
	socket.onerror = ErrorHandler;
	socket.onmessage = empfange;
}

// Listener fÃ¼r den Button go
function send(event) {
	var button = event.target;
	var outmessage = window.document.getElementById("userName").value;
	var nameUsed = false;

	var playerDivs = document.getElementsByClassName("playerDiv");

	for (var i = 0; i < playerDivs.length; i++) {

		if (document.getElementById("player" + i).innerHTML === outmessage) {
			alert("Name schon vergeben");
			nameUsed = true;
		}
	}

	if (!nameUsed && outmessage !== "") {

		var newPlayer = JSON.stringify({
			"NEWPLAYER" : outmessage
		});
		var string = newPlayer;
		var obj = JSON.parse(string);

		if (bereitZumSenden == true) {
			socket.send(newPlayer);
			removeLoginButton();
		} else
			alert("Server noch nicht bereit zum Empfangen. Bitte nochmals versuchen");
	}
}

function sendenMoeglich() {
	bereitZumSenden = true;
	console.log("Senden ist möglich");
}

function ErrorHandler(event) {
	alert("Fehler bei den Websockets " + event.data);
}

function Closing(event) {

	var string = JSON.stringify({
		"LOGOUT" : true
	});
	socket.send(string);
}

function empfange(message) {

	var text = message.data;
	var json = JSON.parse(text);

	if (json.CATALOG) {

		console.log("Hab katalog bekommen");
		var catName = json.CATALOG;
		if (!firstPlayer) {
			setTmpCat(catName);
			tmpCat = true;
		}
	}

	if (json.PLAYERLIST) {

		var playerDiv = document.getElementById("players");

		while (playerDiv.hasChildNodes()) {
			playerDiv.removeChild(playerDiv.lastChild);
		}

		for (var i = 0; i <= json.PLAYERLIST.length; i++) {
			var div = document.createElement("div");
			div.className = "playerDiv";
			div.id = "player" + i;
			var divname = document.createTextNode(json.PLAYERLIST[i].username);
			div.appendChild(divname);
			playerDiv.appendChild(div);
		}
	}

	if (json.GAMESTART) {

		console.log("spiel kann gestartet werden");
		createGameStartButton();
	}

	if (json.LEADER) {

		firstPlayer = true;
		initCatListener();
		setLeaderText();
	}

	if (json.QUESTION) {
		gameOn();
		tableCreate();
		var question = json.QUESTION;
		createQuestion(question);
	}
}

function createGameStartButton() {

	var Button = document.createElement("input");
	Button.type = "button";
	Button.value = "Start Game";
	Button.id = "Button";

	var loginDiv = document.getElementById("loginForm");
	loginDiv.appendChild(Button);
	Button.addEventListener("click", startGame, false);
}

function startGame() {

	if (tmpCat) {
		console.log("ALARM START ");
		var start = JSON.stringify({
			"GAMESTART" : true
		});
		socket.send(start);
	}else{
		alert("first pick cat!");
	}
}

function catalogSelected(name) {
	
	tmpCat = true;
	var cat = JSON.stringify({
		"CATALOG" : name
	});
	socket.send(cat);
}

function gameOn() {
	var questionSection = document.createElement("section");
	questionSection.id = "newMain";
	var frameDiv = document.getElementById("frame");
	var mainSection = document.getElementById("main");
	frameDiv.replaceChild(questionSection, mainSection);
}

function createQuestion(question) {

	document.getElementById("questionText").innerHTML = question[0];
	for (var aC = 0; aC < 4; aC++) {
		document.getElementById("answer" + aC).innerHTML = question[aC + 1];
	}
	// Noch Feld/Platz finden/designen
	timer = question[5]/1000;
	document.getElementById("timerRow").innerHTML = "Seconds left:  " + timer;
	initTimer();
}

function tableCreate() {
	var questionSection = document.getElementById("newMain"), tbl = document
			.createElement('table');
	tbl.id = "showQuestionTable";

	var aC = 0;
	for (var i = 0; i < 3; i++) {
		var tr = tbl.insertRow();
		for (var j = 0; j < 2; j++) {
			var tc = tr.insertCell();
			if (i !== 0) {
				tc.id = "answer" + aC;
				tc.className = "cols";
				aC++;
			} else {
				if (j === 0) {
					tc.setAttribute("colspan", "2");
					tc.id = "questionText";
				}
			}
			tc.addEventListener("click", answerClicked, false);
		}
	}
	
	var timerRow = document.createElement("div");
	timerRow.id = "timerRow";
	
	questionSection.appendChild(tbl);
	questionSection.appendChild(timerRow);
}

function removeLoginButton() {
	console.log("RemoveLoginButton");
	var btn = document.getElementById("loginButton");
	var userName = document.getElementById("userName");
	var loginForm = document.getElementById("loginForm");

	loginForm.removeChild(btn);

	
	var textDiv = document.createElement("div");
	textDiv.id = "text";
	textDiv.innerHTML = "Please wait until Leader starts the game!";
	loginForm.replaceChild(textDiv, userName);
}

function setLeaderText() {
	console.log("setLeaderText");
	var leaderTextDiv = document.getElementById("text");
	leaderTextDiv.innerHTML = "Please select a catalog!" 
		+ "<br />" + "You can start the game as soon as a second player joins!";
}

function initTimer(){
	alert("Timer in sec: " + timer);	
	animation = window.setInterval("countTime();", 1000);
}

function countTime(){

	if(timer == 0){
		window.clearInterval(animation);
		alert("Frage nicht beantwortet!");
		document.getElementById("timerRow").innerHTML = "Seconds left:   0";
	}else{
		document.getElementById("timerRow").innerHTML = "Seconds left:  " + timer;
	}
	timer = timer -1;
}

function answerClicked(event){
	var answerClicked = event.target;

	for(var i = 0; i < 4; i++){
		if(answerClicked === document.getElementById("answer"+i)){
			alert("Answer " + i + " picked! :)");
		}
	}
}


//document.addEventListener('DOMContentLoaded', initLogin, false);
// 
// var socket;
// var bereitZumSenden=false;
// 
// function initLogin()
// {  // Listener registrieren fÃ¼r Buttons
//	var login= window.document.getElementById("loginButton");   
//	login.addEventListener("click",send,false); 
//
//	//var url = "ws://" + document.location.host  + "/Echo";
//	var url = 'ws://localhost:8080/WebQuizSS15/Login';
////	alert("url= "+url);
//	
//	socket=new WebSocket(url);
//
//	socket.onopen=sendenMoeglich;
//	socket.onclose=Closing;
//	socket.onerror=ErrorHandler;
//	socket.onmessage=empfange;
// }
//
// 
//// Listener fÃ¼r den Button go
// function send(event)
// {  
// 	var button = event.target;
// 	var outmessage=window.document.getElementById("userName").value; 
// 	var newPlayer =JSON.stringify({"NEWPLAYER":outmessage});
// 	
// 	var string =newPlayer;
// 	var obj = JSON.parse(string);
// 	
//// 	var string = JSON.stringify(obj);
// 
// 	if ( bereitZumSenden == true)
// 	    { alert("Jetzt senden");socket.send(newPlayer);}
// 	else alert("Server noch nicht bereit zum Empfangen. Bitte nochmals versuchen");
//	 
//	
// } 
// 
// function sendenMoeglich(){ bereitZumSenden=true; console.log("Senden ist möglich");}
// function ErrorHandler(event){ alert("Fehler bei den Websockets "+event.data);}
// function Closing(event){ 
//	 alert("Websockets closing "+event.code);
//	 
//	 
//	 var string = JSON.stringify({"LOGOUT":true});
//	 socket.send(string);
// 
// }
// 
// function empfange(message)
// { 
//	 var text = message.data;
////	 alert(text);
//	 var json=JSON.parse(text);
////	alert("json:"+json);
////	 alert(json[0].username);
//	 
//	 if(json.QUESTION){
//		 alert("Question erhalten");
//		 console.log(json.QUESTION[0]);
//		 console.log(json.QUESTION[1]);
//	 }
//	 if(json.CATALOG){
////		 alert(json.CATALOG);
//		 console.log("Hab katalog bekommen");
//	 }
//	 if(json.PLAYERLIST){
////		 alert("playerlist"+json.Playerlist);
////		 var i=0;
////		 
////		 for(i;i<json.Playerlist.length;i++){
////			 alert("Name:"+json.Playerlist[i].username)
////		 }
//		
//		 
//	
//	 var playerDiv = document.getElementById("players");
////	 var frameDiv = document.getElementById("frame");
////	 frameDiv.removeChild(scoreDiv);
////	 var score= document.createElement("div");
////	 score.id="score";
//	 
//	 while(playerDiv.hasChildNodes()){
//		 playerDiv.removeChild(playerDiv.lastChild);
//	 }
//	 
//	 for(var i=0;i<=json.PLAYERLIST.length;i++){
//		 var div=document.createElement("div");
//			div.className="playerDiv";
//			div.id="player"+i;
//			
//			var divname = document.createTextNode(json.PLAYERLIST[i].username);
//			
//			div.appendChild(divname);
//			
//			playerDiv.appendChild(div);
//		 
//	 }
//	 
//	  }
//	 if(json.GAMESTART){
//		 console.log("spiel kann gestartet werden");
//		 
//		 createGameStartButton();
//	 }
//	
////	 frameDiv.appendChild(score);
//// 	var text = message.data;
//// 	//alert("emfange Daten vom Server: "+text);
//// 	// Textfeld zur Ausgabe des Echos vom Server
//// 	var output=window.document.getElementById("ausgabetext");
//// 	// Bereich zur Darstellung aller angemeldeten Clients
//// 	var session=window.document.getElementById("sessionID"); 
//// 	
//// 	// es kommt ein JSON-Array mit der Liste aller Clients
//// 	if (text.charAt(0) == "[")
//// 	{       // JSON-String in ein Array wandeln
//// 		    var clientliste = JSON.parse(text);
//// 		    
//// 		    output="";
//// 		    // Liste aller Clients anzeigen
//// 	        for ( i=0; i<clientliste.length; i++)  output = output+"client "+i+":"+clientliste[i]+"<br/>";
//// 	            session.innerHTML=output;
////    }
//// 	// es kommt ein Echo 
//// 	else 	output.value=text;
//	
// } 
// 
// function createGameStartButton(){
//	 
//	 var Button = document.createElement("input");
//	 Button.type="button";
//	 Button.value="Start Game";
//	 Button.id="Button";
//	 
//	 var loginDiv=document.getElementById("loginForm");
//	 loginDiv.appendChild(Button);
//	 Button.addEventListener("click",startGame,false);
//
// }
// 
// function startGame(){
//	 console.log("ALARM START ");
//	 var start =JSON.stringify({"GAMESTART":true});
//	 socket.send(start);
// }
// function catalogSelected(name){
//	 console.log("Name"+name);
//	 var cat =JSON.stringify({"CATALOG":name});
//	 socket.send(cat);
// }