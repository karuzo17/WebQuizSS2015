document.addEventListener('DOMContentLoaded', initLogin, false);
window.onbeforeunload = tabClose;
var socket;
var bereitZumSenden;
var firstPlayer;
var tmpCat;
var animation;
var timer;
var question;
var sendAnswer;
var firstQuestion;
var waitingForResponse;
var nameOfPlayer;

function initLogin() {

	initPlayerTable();
	defaultSettings();
	setConnection();
}

function setConnection(){
	var url = 'ws://localhost:8080/WebQuizSS15/Login';

	socket = new WebSocket(url);

	socket.onopen = sendenMoeglich;
	socket.onclose = Closing;
	socket.onerror = ErrorHandler;
	socket.onmessage = empfange;
}

function defaultSettings() {
	
	bereitZumSenden = false;
	firstPlayer = false;
	tmpCat = false;
	firstQuestion = true;
	waitingForResponse = false;
	
	for (var i = 0; i < 6; i++) {
		document.getElementById("playerCol" + i).innerHTML = "-";
		document.getElementById("scoreCol" + i).innerHTML = 0;
		document.getElementById("rows" + i).style.background = "Whitesmoke";
	}

	var catalogsTMP = document.getElementsByClassName("catalogDiv");
	for (var j = 0; j < catalogsTMP.length; j++) {
		document.getElementById("catalog" + j).style.background = "Whitesmoke";
	}

	var main = document.getElementById("main");
	document.getElementById("main").innerHTML = "";

	var loginForm = document.createElement("div");
	loginForm.id = "loginForm";

	main.appendChild(loginForm);

	var loginButton = document.createElement("input");
	loginButton.type = "button";
	loginButton.value = "Login";
	loginButton.id = "loginButton";
	loginButton.addEventListener("click", send, false);

	var userName = document.createElement("input");
	userName.type = "text";
	userName.name = "userName";
	userName.id = "userName";

	loginForm.appendChild(userName);
	loginForm.appendChild(loginButton);
}


function send(event) {
	
	var button = event.target;
	var outmessage = window.document.getElementById("userName").value;
	var nameUsed = false;
	var playerCols = document.getElementsByClassName("playerCol");

	for (var i = 0; i < playerCols.length; i++) {

		if (document.getElementById("playerCol" + i).innerHTML === outmessage) {
			alert("Name schon vergeben");
			nameUsed = true;
		}
	}

	if (!nameUsed && outmessage !== "") {

		var newPlayer = JSON.stringify({
			"NEWPLAYER" : outmessage
		});
		// var string = newPlayer;
		// var obj = JSON.parse(string);
		nameOfPlayer = outmessage;

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
	
}

function Closing(event) {

}

function tabClose(event) {
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
		
		console.log("PlayerListLength: " + json.PLAYERLIST.length);
		var playerTable = document.getElementById("playerTable");

		for (var i = 0; i < 6; i++) {

			if (json.PLAYERLIST[i].username == nameOfPlayer) {
				document.getElementById("rows" + i).style.background = "#7C907A";
			} else {
				document.getElementById("rows" + i).style.background = "Whitesmoke";
			}

			if (i < json.PLAYERLIST.length) {
				document.getElementById("playerCol" + i).innerHTML = json.PLAYERLIST[i].username;
				document.getElementById("scoreCol" + i).innerHTML = json.PLAYERLIST[i].score;
			} else {
				document.getElementById("playerCol" + i).innerHTML = "-";
				document.getElementById("scoreCol" + i).innerHTML = 0;
			}
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

		if (firstQuestion) {
			firstQuestion = false;
			cleanMain();
			tableCreate();
		}

		var question = json.QUESTION;
		createQuestion(question);
	}

	if (json.WAIT) {
		
		setMainTextWait();
	}

	if (json.ERROR) {

		alert("Got error: " + json.ERROR);
	}

	if (json.RESPONSE) {

		console.log("Hab die Antwort erhalten");
		var correctAnswer = json.RESPONSE;

		setAnswerBackground(correctAnswer);

		console.log("korrekte Antwort" + correctAnswer);
		console.log("gewähöte Antwort" + sendAnswer);

		setTimeout(sendResponse, 3000);
	}

	if (json.GAMEOVER) {
		defaultSettings();
	}

	if (json.RANK) {
		var rank = json.RANK;
		setMainTextRank(rank);
		
		//default settings zum Test
		//defaultSettings();
	}

	if (json.NEWGAME) {

		defaultSettings();
	}
}

function sendResponse() {

	waitingForResponse = false;
	var question = JSON.stringify({
		"QUESTION" : true
	});
	socket.send(question);
}

function setAnswerBackground(correctA) {

	console.log("answer background");

	var cols = document.getElementsByClassName("cols");

	if (sendAnswer === correctA) {
		document.getElementById("answer" + correctA).style.background = "#BEF781";
	} else if (sendAnswer === 4) {
		document.getElementById("answer" + correctA).style.background = "#BEF781";
	} else {
		document.getElementById("answer" + sendAnswer).style.background = "#F78181";
		document.getElementById("answer" + correctA).style.background = "#BEF781";
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
	} else {
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

function cleanMain() {
	var mainSection = document.getElementById("main");

	while (mainSection.hasChildNodes()) {
		mainSection.removeChild(mainSection.lastChild);
	}
}

function createQuestion(questionFromServer) {

	for (var aC = 0; aC < 4; aC++) {
		document.getElementById("answer" + aC).innerHTML = questionFromServer[aC + 1];
		document.getElementById("answer" + aC).style.background = "Whitesmoke";
	}

	timer = questionFromServer[5] / 1000;
	question = questionFromServer[0];
	document.getElementById("questionText").innerHTML = questionFromServer[0]
			+ "<br /><br /><br />" + "Seconds left:  " + timer;
	initTimer();
}

function tableCreate() {
	var questionSection = document.getElementById("main");
	tbl = document.createElement('table');
	tbl.id = "showQuestionTable";

	var aC = 0;
	for (var i = 0; i < 3; i++) {
		var tr = tbl.insertRow();
		for (var j = 0; j < 2; j++) {
			var tc = tr.insertCell();
			if (i !== 0) {
				tc.id = "answer" + aC;
				tc.className = "cols";
				tc.setAttribute("width", "55%");
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

	questionSection.appendChild(tbl);
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
	leaderTextDiv.innerHTML = "Please select a catalog!" + "<br />"
			+ "You can start the game as soon as a second player joins!";
}

function initTimer() {
	animation = window.setInterval("countTime();", 1000);
}

function countTime() {

	var questionText = document.getElementById("questionText");

	timer = timer - 1;
	if (timer == 0) {
		questionText.innerHTML = question + "<br /><br /><br />"
				+ "Seconds left:   0";
		window.clearInterval(animation);
		var response = JSON.stringify({
			"RESPONSE" : 4
		});
		sendAnswer = 4;
		console.log("sendAnswer" + sendAnswer);
		waitingForResponse = true;
		socket.send(response);
	} else {
		questionText.innerHTML = question + "<br /><br /><br />"
				+ "Seconds left:  " + timer;
	}
}

function answerClicked(event) {
	var answerClicked = event.target;
	console.log("AnswerClicked");
	if (!waitingForResponse) {
		console.log("IF___AnswerClicked");
		for (var i = 0; i < 4; i++) {
			if (answerClicked === document.getElementById("answer" + i)) {
				sendAnswer = i;
				window.clearInterval(animation);
				var answer = JSON.stringify({
					"RESPONSE" : sendAnswer
				});
				socket.send(answer);
				console.log("Response in answerClicked versendet");
				waitingForResponse = true;
			}
		}
	}
}

function initPlayerTable() {

	var playersDiv = document.getElementById("players");
	var playerTable = document.createElement("table");
	playerTable.id = "playerTable";
	playerTable.setAttribute("width", "90%");

	for (var i = 0; i < 6; i++) {

		var tableRow = playerTable.insertRow();
		tableRow.id = "rows" + i;

		for (var j = 0; j < 3; j++) {
			var tableCell = tableRow.insertCell();
			if (j === 0) {
				tableCell.innerHTML = i + 1 + ".";
				tableCell.setAttribute("width", "20%");
			} else if (j === 1) {
				tableCell.id = "playerCol" + i;
				tableCell.innerHTML = "-";
				tableCell.className = "playerCol";
			} else {
				tableCell.id = "scoreCol" + i;
				tableCell.className = "scoreCol";
				tableCell.innerHTML = "-";
			}
		}
	}
	playersDiv.appendChild(playerTable);
}

function setMainTextWait() {
	cleanMain();
	var waitText = document.createElement("div");
	waitText.id = "texts";
	waitText.innerHTML = "GAME OVER! Please wait for the Final result!";
	document.getElementById("main").appendChild(waitText);
}

function setMainTextRank(rank) {
	cleanMain();
	var rankText = document.createElement("div");
	rankText.id = "texts";
	rankText.innerHTML = "You finished on rank " + rank
			+ "! Congrats and thanks for playing! :)";
	document.getElementById("main").appendChild(rankText);
}