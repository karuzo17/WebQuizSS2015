//Eventlistener, der die initLogin()-Funktion beim Laden des Dokumentes aufruft
//und damit die Initialisierung des Quiz beginnt
document.addEventListener('DOMContentLoaded', initLogin, false);

//Falls sich ein Spieler per TabClose abmeldet, wir die "onbeforeunload"-Funktion
//aufgerufen, damit man die Verbindung mithilfe eines Logout-Pakets beenden kann.
window.onbeforeunload = tabClose;

//Variable zum Verbindungsaufbau
var socket;

//Zustand des Sockets
var bereitZumSenden;

//Der erste Spieler bekommt ein "Leader"-Paket und setzt dann diese Variable auf "true",
//bekommt dann mehr "Rechte" als die anderen Spieler
var firstPlayer;

//der vor Spielbeginn ausgewählte Katalog, darf beim Klick auf den "GameStart"-Button nicht "" sein
var tmpCat;

//wird nicht mehr genutzt, hat aber die Laufschrift im Header ermöglicht
var animation;

//zählt die Sekunden runter, innerhalb der Spieler die Fragen beantworten muss 
var timer;

//in diese Variable wird die Frage hineinkopiert, damit sie auch an anderen Stellen benutzt werden kann
var question;

//der Index der Antwort, die der Spieler angeklickt hat und der zum Server zum Überprüfen gesendet wird
var sendAnswer;

//überprüft ob eine Frage die erste Frage ist, entsprechend werden Fragen erstellt bzw die Tabelle erstellt
var firstQuestion;

//nachdem der Spieler dem Server seine Antwort auf eine Frage gesendet hat, wartet er auf eine Antwort,
//in dieser Zeit kann er keine weiteren Antworten  anklicken
var waitingForResponse;

//speichert den Spielernamen, auf den immer bei der PLAYERLIST überprüft wird, um ihn dann grün zu hinterlegen
var nameOfPlayer;

//die Funktion wird einmal beim Laden des Dokuments aufgerufen, sie erstellt die Spielertablle unten rechts
//und initialisiert die defaultSettings, die auch bei einem Neustart nach Beenden des Spiels den Zustand zurücksetzt
function initLogin() {

	initPlayerTable();
	defaultSettings();
}

//verbindet den Client mit dem Server
function setConnection(){
	
	//die URL zum Server
	var url = 'ws://localhost:8080/WebQuizSS15/Login';
	
	//neue Verbindung wird hergestellt
	socket = new WebSocket(url);

	//die Zustände des Sockets werden mit entsprechenden Funktionen behandelt
	socket.onopen = sendenMoeglich;
	socket.onclose = Closing;
	socket.onerror = ErrorHandler;
	socket.onmessage = empfange;
}

//allgemeinde Standardeinstellungen, bevor ein Spiel gestartet wird
function defaultSettings() {
	
	//siehe Erklärung am Dateibeginn
	bereitZumSenden = false;
	firstPlayer = false;
	tmpCat = false;
	firstQuestion = true;
	waitingForResponse = false;
	
	//Diese Schleife geht durch die komplette Spielertabelle und setzt Namen auf "", Punkte 
	//auf 0 und den Hintergrund auf den Standard "Whitesmoke"
	for (var i = 0; i < 6; i++) {
		document.getElementById("playerCol" + i).innerHTML = "-";
		document.getElementById("scoreCol" + i).innerHTML = 0;
		document.getElementById("rows" + i).style.background = "Whitesmoke";
	}

	//der zuvor ausgewählte Katalog wird nicht mehr gehighlightet.
	var catalogsTMP = document.getElementsByClassName("catalogDiv");
	for (var j = 0; j < catalogsTMP.length; j++) {
		document.getElementById("catalog" + j).style.background = "Whitesmoke";
	}
	
	//der Div, der für die Textausgabe in der "main"-Section zuständig ist, wird auf den default 
	//gesetzt
	var main = document.getElementById("main");
	document.getElementById("main").innerHTML = "";
	
	//dem "main"-Div wird nach Reset die Anmeldemöglichkeit wiederhergestellt und angehängt
	var loginForm = document.createElement("div");
	loginForm.id = "loginForm";
	main.appendChild(loginForm);

	//Login-button wird erzeugt
	var loginButton = document.createElement("input");
	loginButton.type = "button";
	loginButton.value = "Login";
	loginButton.id = "loginButton";
	loginButton.addEventListener("click", send, false);

	//input-Feld für den Benutzernamen wird erzeugt
	var userName = document.createElement("input");
	userName.type = "text";
	userName.name = "userName";
	userName.id = "userName";

	//button und inputfeld werden loginForm hinzugefügt
	loginForm.appendChild(userName);
	loginForm.appendChild(loginButton);
	
	//Verbindung zum Server wird hergestellt
	setConnection();
}

//wird aufgerrufen wenn der Login-button geklickt wurde
function send(event) {
	
	//button der geklickt wurde
	var button = event.target;
	
	//Spielername, der dem Server gesendet werde soll
	var outmessage = window.document.getElementById("userName").value;
	
	//mit der Variable wird auf schon vorhandene Namen reagiert
	var nameUsed = false;
	var playerCols = document.getElementsByClassName("playerCol");

	//alle bisher angemeldeten Spieler werden auf den eingegebenen Namen überprüft
	for (var i = 0; i < playerCols.length; i++) {
		if (document.getElementById("playerCol" + i).innerHTML === outmessage) {
			alert("Name schon vergeben");
			nameUsed = true;
		}
	}

	//falls das input-Feld nicht null ist und der name nicht schon verwendet, wird dem 
	//Server ein NEWPLAYER-Paket gesendet
	if (!nameUsed && outmessage !== "") {

		//erstellen des JSON-Objekts
		var newPlayer = JSON.stringify({
			"NEWPLAYER" : outmessage
		});
		
		//Spielername wird gespeichert, damit der Client weiß wie er heißt
		nameOfPlayer = outmessage;

		//senden des Objekts
		if (bereitZumSenden == true) {
			socket.send(newPlayer);
			
			//entfernen des Login-buttons, und einblenden des Wartetexts
			removeLoginButton();
		} else
			alert("Server noch nicht bereit zum Empfangen. Bitte nochmals versuchen");
	}
}

//Verbindung ist erstellt und senden möglich
function sendenMoeglich() {
	
	bereitZumSenden = true;
	console.log("Senden ist möglich");
}

function ErrorHandler(event) {
}

//diese Funktion ist durch die Benutzung der tabClose(event)-Funktion überfällig (wenn auch nicht für
//alle Arten)
function Closing(event) {
}

//sendet dem Server ein LOGOUT-Paket, bevor die Verbindung abgebrochen wird
function tabClose(event) {
	
	var string = JSON.stringify({
		"LOGOUT" : true
	});
	socket.send(string);
}

//Funktion die Pakete vom Server empfängt und entsprechend weitere Funktionen aufruft
function empfange(message) {

	//auslesen des Paketinhalts
	var text = message.data;
	var json = JSON.parse(text);

	//bekommt ein Client ein CATALOG-Paket, wird der dort erwähnte Katalog gehighlightet
	if (json.CATALOG) {

		console.log("Hab katalog bekommen");
		var catName = json.CATALOG;
		if (!firstPlayer) {
			setTmpCat(catName);
			tmpCat = true;
		}
	}
	
	//diese Funktion ist für die Darstellung der Highscoretabelle zuständig
	if (json.PLAYERLIST) {
		
		var playerTable = document.getElementById("playerTable");

		//setzt Namen und Punkt an Stellen in denen Spieler angemeldet sind, ansonsten wird die
		//Tabelle mit default-Werten belegt
		for (var i = 0; i < 6; i++) {
			if (i < json.PLAYERLIST.length) {
				document.getElementById("playerCol" + i).innerHTML = json.PLAYERLIST[i].username;
				document.getElementById("scoreCol" + i).innerHTML = json.PLAYERLIST[i].score;
			} else {
				document.getElementById("playerCol" + i).innerHTML = "-";
				document.getElementById("scoreCol" + i).innerHTML = 0;
			}
		}
		
		//alle Tabellenzeilen werden mit dem Standard hintergrund versehen...
		for(var k=0; k< 6;k++){
			document.getElementById("rows" + k).style.background = "Whitesmoke";
		}
		
		//... damit im nächsten Schritt die Zeile mit dem eigenen Spielernamen gehighlightet werden kann
		for(var j =0 ; j <json.PLAYERLIST.length;j++){
			if (json.PLAYERLIST[j].username == nameOfPlayer) {
				document.getElementById("rows" + j).style.background = "#7C907A";
			}
		}
	}

	//nur der Spielleiter bekommt das Paket, er erhält einen GameStart-Button und besitzt nun so die Möglichkeit
	//das Spiel zu starten
	if (json.GAMESTART) {

		console.log("spiel kann gestartet werden");
		createGameStartButton();
	}

	//der Spielleiter erhält dieses Paket, infolge dessen erhält er Privilegien und kann so zB. Kataloge
	//auswählen
	if (json.LEADER) {

		firstPlayer = true;
		initCatListener();
		setLeaderText();
	}

	//Frage, die jeder Client darstellt
	if (json.QUESTION) {

		//bei der ersten Frage wird einmalig die Tabelle zur Fragendarstellung gestellt
		if (firstQuestion) {
			firstQuestion = false;
			cleanMain();
			tableCreate();
		}

		//falls es nicht die erste Frage ist wird die Tabelle nur aktualisiert
		var question = json.QUESTION;
		createQuestion(question);
	}

	//nach Beantwortung aller Fragen wartet der Client, bis ALLE Spieler fertig sind
	if (json.WAIT) {
		
		setMainTextWait();
	}

	//Error-Pakete werden ausgegeben
	if (json.ERROR) {

		alert("Got error: " + json.ERROR);
	}

	//Paket des Servers, das die korrekte Antwort auf die letzte Frage enthält
	if (json.RESPONSE) {

		console.log("Hab die Antwort erhalten");
		
		//auslesen des korrekten Index
		var correctAnswer = json.RESPONSE;

		//Hintergrund der Antworten entsprechend anpasse (grün hinterlegen für korrekt,
		//rot für falsch)
		setAnswerBackground(correctAnswer);

		//nach einem Timeout zur Darstellung der Lösung wird nach einer neuen Frage verlangt
		setTimeout(sendResponse, 3000);
	}

	//nach der Darstellung der RANKS bei Spielende wird bei Empfang des GAMEOVER-Pakets der
	//default-Zustand wiederhergestellt
	if(json.GAMEOVER){

		defaultSettings();
	}

	//der Rang nach Spielende wird dem Client mitgeteilt, nach einem Timeout wird dem Server ein 
	//GAMEOVER-Paket gesendet
	if(json.RANK){

		setMainTextRank(json.RANK);
		setTimeout(sendGameOver,3000);
	}
}

//nach Beantwortung einer Frage sendet der Client ein QUESTION-Paket, mit der er eine neue anfordert
function sendResponse() {

	waitingForResponse = false;
	var question = JSON.stringify({
		"QUESTION" : true
	});
	socket.send(question);
}

//die "divs" der jeweiligen Antworten werden entsprechend der eigenen Antwort hinterlegt (oder aber es wurde
//keine Antwort gegeben (sendAnswer ===4))
function setAnswerBackground(correctA) {

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

//sobald zwei Spieler angemeldet sind, bekommt der Spielleiter die Möglichkeit das Spiel zu starten
function createGameStartButton() {

	//erstellen des Buttons
	var Button = document.createElement("input");
	Button.type = "button";
	Button.value = "Start Game";
	Button.id = "Button";

	//hinzufügen des Buttons
	var loginDiv = document.getElementById("loginForm");
	loginDiv.appendChild(Button);
	Button.addEventListener("click", startGame, false);
}

//wird aufgerufen wenn der Spielleiter den StartGame-Button klickt
function startGame() {

	//falls schon ein Katalog ausgewählt wurde wird das entsprechende Paket gesendet
	if (tmpCat) {
		console.log("ALARM START ");
		var start = JSON.stringify({
			"GAMESTART" : true
		});
		socket.send(start);
		
		//bei Neustart des Spiels dürfen alle Clients nicht in der Lage sein, Kataloge
		//auszuwählen, der einzige im Spiel (Spielleiter) bekommt hier die Rechte entzogen
		removeCatListener();
	} else {
		alert("first pick cat!");
	}
}

//wird gesendet nachdem der RANK angezeigt wurde
function sendGameOver(){
	var gameover = JSON.stringify({
		"GAMEOVER" : true
	});
	socket.send(gameover);
}

//sendet den aktuell ausgewählten Katalog des Spielleiters zum Server, der diesen dann weiterverteilt
function catalogSelected(name) {

	tmpCat = true;
	var cat = JSON.stringify({
		"CATALOG" : name
	});
	socket.send(cat);
}

//die "main"-Section wird von allen ChildNodes befreit, damit die Fragentabelle erstellt werden kann
function cleanMain() {
	var mainSection = document.getElementById("main");

	while (mainSection.hasChildNodes()) {
		mainSection.removeChild(mainSection.lastChild);
	}
}

//schreibt die aktuelle Frage in die Tabelle 
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

//document.addEventListener('DOMContentLoaded', initLogin, false);
//window.onbeforeunload=tabClose;
//var socket;
//var bereitZumSenden = false;
//var firstPlayer = false;
//var tmpCat = false;
//var animation;
//var timer;
//var question;
//var sendAnswer;
//var firstQuestion = true;
//var waitingForResponse = false;
//
//// Dieser Text ist nur zum Pushen da
//// Dieser Text ist nur zum Pushen da
//// Dieser Text ist nur zum Pushen da
//// Dieser Text ist nur zum Pushen da
//// Dieser Text ist nur zum Pushen da
//// Dieser Text ist nur zum Pushen da
//
//function initLogin() { // Listener registrieren fÃ¼r Buttons
//
//	startNewGame();
//
//	initPlayerTable();
//
//	var url = 'ws://localhost:8080/WebQuizSS15/Login';
//
//	socket = new WebSocket(url);
//
//	socket.onopen = sendenMoeglich;
//	socket.onclose = Closing;
//	socket.onerror = ErrorHandler;
//	socket.onmessage = empfange;
//}
//
//function startNewGame() {
//
//	var main = document.getElementById("main");
//
//	var loginForm = document.createElement("div");
//	loginForm.id = "loginForm";
//
//	main.appendChild(loginForm);
//
//	var loginButton = document.createElement("input");
//	loginButton.type = "button";
//	loginButton.value = "Login";
//	loginButton.id = "loginButton";
//	loginButton.addEventListener("click", send, false);
//
//	var userName = document.createElement("input");
//	userName.type = "text";
//	userName.name = "userName";
//	userName.id = "userName";
//
//	loginForm.appendChild(loginButton);
//	loginForm.appendChild(userName);
//
//}
//
//// Listener fÃ¼r den Button go
//function send(event) {
//	var button = event.target;
//	var outmessage = window.document.getElementById("userName").value;
//	var nameUsed = false;
//	var playerCols = document.getElementsByClassName("playerCol");
//
//	for (var i = 0; i < playerCols.length; i++) {
//
//		if (document.getElementById("playerCol" + i).innerHTML === outmessage) {
//			alert("Name schon vergeben");
//			nameUsed = true;
//		}
//	}
//
//	if (!nameUsed && outmessage !== "") {
//
//		var newPlayer = JSON.stringify({
//			"NEWPLAYER" : outmessage
//		});
//		var string = newPlayer;
//		var obj = JSON.parse(string);
//
//		if (bereitZumSenden == true) {
//			socket.send(newPlayer);
//			removeLoginButton();
//		} else
//			alert("Server noch nicht bereit zum Empfangen. Bitte nochmals versuchen");
//	}
//}
//
//function sendenMoeglich() {
//	bereitZumSenden = true;
//	console.log("Senden ist möglich");
//}
//
//function ErrorHandler(event) {
//	alert("Fehler bei den Websockets " + event.data);
//}
//
//function Closing(event) {
//
//	
//}
//function tabClose(event){
//	var string = JSON.stringify({
//		"LOGOUT" : true
//	});
//	socket.send(string);
//}
//
///**
// * @param message
// */
///**
// * @param message
// */
//function empfange(message) {
//
//	var text = message.data;
//	var json = JSON.parse(text);
////	console.log(json);
//
//
//	if (json.CATALOG) {
//
//		console.log("Hab katalog bekommen");
//		var catName = json.CATALOG;
//		if (!firstPlayer) {
//			setTmpCat(catName);
//			tmpCat = true;
//		}
//	}
//
//	if (json.PLAYERLIST) {
//
//		var playerTable = document.getElementById("playerTable");
//
//		for (var i = 0; i < 6; i++) {
//
//			if (i < json.PLAYERLIST.length) {
//				document.getElementById("playerCol" + i).innerHTML = json.PLAYERLIST[i].username;
//				document.getElementById("scoreCol" + i).innerHTML = json.PLAYERLIST[i].score;
//			} else {
//				document.getElementById("playerCol" + i).innerHTML = "-";
//				document.getElementById("scoreCol" + i).innerHTML = 0;
//			}
//		}
//	}
//
//	if (json.GAMESTART) {
//
//		console.log("spiel kann gestartet werden");
//		createGameStartButton();
//	}
//
//	if (json.LEADER) {
//
//		firstPlayer = true;
//		initCatListener();
//		setLeaderText();
//	}
//
//	if (json.QUESTION) {
//
//		if (firstQuestion) {
//			firstQuestion = false;
//			cleanMain();
//			tableCreate();
//		}
//
//		var question = json.QUESTION;
//		createQuestion(question);
//	}
//	if(json.WAIT){
////		alert("Warte bis alle Spieler fertig sind");
//		setMainTextWait();
//	}
//
//	if (json.ERROR) {
//
//		alert("Got error:");
//	}
//
//	if (json.RESPONSE) {
//		
//		console.log("Hab die Antwort erhalten");
//		var correctAnswer = json.RESPONSE[0];
//		
//		setAnswerBackground(correctAnswer);
//		console.log("korrekte Antwort"+correctAnswer);
//		console.log("gewähöte Antwort"+sendAnswer);
//		setTimeout(function() {
//			waitingForResponse = false;
//			var question = JSON.stringify({
//				"QUESTION" : true
//			});
//			socket.send(question);
//			
//		}, 3000);
//	}
//	
//	if(json.GAMEOVER){
//		//zu implementieren
//		alert("GAMEOVER")
////		defaultSettings();
////		
//		
//	}
//
//	if(json.RANK){
////		console.log("Du hast "+json.RANK+" erreicht");
//		//zu implementieren
//		setMainTextRank(json.RANK);
//		setTimeout(3000,sendGameOver());
//	}
//
//	if(json.ERROR){
//
//		//zu implementieren
//		alert(json.ERROR);
//	}
//}
//
//function setMainTextWait() {
//	cleanMain();
//	var waitText = document.createElement("div");
//	waitText.id = "texts";
//	waitText.innerHTML = "GAME OVER! Please wait for the Final result!";
//	document.getElementById("main").appendChild(waitText);
//}
//
//function setMainTextRank(rank) {
//	cleanMain();
//	var rankText = document.createElement("div");
//	rankText.id = "texts";
//	rankText.innerHTML = "You finished on rank " + rank
//			+ "! Congrats and thanks for playing! :)";
//	document.getElementById("main").appendChild(rankText);
//}
//
//function setAnswerBackground(correctA) {
//	
//	console.log("answer background");
////	alert("drin!");
//
//	var cols = document.getElementsByClassName("cols");
//
//	if(sendAnswer === correctA){
//		document.getElementById("answer" + correctA).style.background = "#BEF781";
//	} 
//	else if(sendAnswer === 4){
////		alert("deine mom stinkt");
//		document.getElementById("answer" + correctA).style.background = "#BEF781";
//	}else{
//		document.getElementById("answer" + sendAnswer).style.background = "#F78181";
//		document.getElementById("answer" + correctA).style.background = "#BEF781";
//	}
//}
//
//function createGameStartButton() {
//
//	var Button = document.createElement("input");
//	Button.type = "button";
//	Button.value = "Start Game";
//	Button.id = "Button";
//
//	var loginDiv = document.getElementById("loginForm");
//	loginDiv.appendChild(Button);
//	Button.addEventListener("click", startGame, false);
//}
//
//function sendGameOver(){
//	var gameover = JSON.stringify({
//		"GAMEOVER" : true
//	});
//	socket.send(gameover);
//}
//function startGame() {
//
//	if (tmpCat) {
//		console.log("ALARM START ");
//		var start = JSON.stringify({
//			"GAMESTART" : true
//		});
//		socket.send(start);
//	} else {
//		alert("first pick cat!");
//	}
//}
//
//function catalogSelected(name) {
//
//	tmpCat = true;
//	var cat = JSON.stringify({
//		"CATALOG" : name
//	});
//	socket.send(cat);
//}
//
//function cleanMain() {
//	var mainSection = document.getElementById("main");
//
//	while (mainSection.hasChildNodes()) {
//		mainSection.removeChild(mainSection.lastChild);
//	}
//}
//
//function createQuestion(questionFromServer) {
//
//	for (var aC = 0; aC < 4; aC++) {
//		document.getElementById("answer" + aC).innerHTML = questionFromServer[aC + 1];
//		document.getElementById("answer" + aC).style.background = "Whitesmoke";
//	}
//
//	timer = questionFromServer[5] / 1000;
//	question = questionFromServer[0];
//	document.getElementById("questionText").innerHTML = questionFromServer[0]
//			+ "<br /><br /><br />" + "Seconds left:  " + timer;
//	initTimer();
//}
//
//function tableCreate() {
//	var questionSection = document.getElementById("main");
//	tbl = document.createElement('table');
//	tbl.id = "showQuestionTable";
//
//	var aC = 0;
//	for (var i = 0; i < 3; i++) {
//		var tr = tbl.insertRow();
//		for (var j = 0; j < 2; j++) {
//			var tc = tr.insertCell();
//			if (i !== 0) {
//				tc.id = "answer" + aC;
//				tc.className = "cols";
//				tc.setAttribute("width", "55%");
//				aC++;
//			} else {
//				if (j === 0) {
//					tc.setAttribute("colspan", "2");
//					tc.id = "questionText";
//				}
//			}
//			tc.addEventListener("click", answerClicked, false);
//		}
//	}
//
//	questionSection.appendChild(tbl);
//}
//
//function removeLoginButton() {
//	console.log("RemoveLoginButton");
//	var btn = document.getElementById("loginButton");
//	var userName = document.getElementById("userName");
//	var loginForm = document.getElementById("loginForm");
//
//	loginForm.removeChild(btn);
//
//	var textDiv = document.createElement("div");
//	textDiv.id = "text";
//	textDiv.innerHTML = "Please wait until Leader starts the game!";
//	loginForm.replaceChild(textDiv, userName);
//}
//
//function setLeaderText() {
//	console.log("setLeaderText");
//	var leaderTextDiv = document.getElementById("text");
//	leaderTextDiv.innerHTML = "Please select a catalog!" + "<br />"
//			+ "You can start the game as soon as a second player joins!";
//}
//
//function initTimer() {
//	animation = window.setInterval("countTime();", 1000);
//}
//
//function countTime() {
//	
//
//	var questionText = document.getElementById("questionText");
//	
//	timer = timer - 1;
//	if (timer == 0) {
//		questionText.innerHTML = question + "<br /><br /><br />"
//				+ "Seconds left:   0";
//		window.clearInterval(animation);
//		var response = JSON.stringify({
//			"RESPONSE" : 4
//		});
//		sendAnswer = 4;
//		console.log("sendAnswer"+sendAnswer);
//		waitingForResponse=true;
//		socket.send(response);
//	} else {
//		questionText.innerHTML = question + "<br /><br /><br />"
//				+ "Seconds left:  " + timer;
//	}
//}
//
//function answerClicked(event) {
//	var answerClicked = event.target;
//	console.log("AnswerClicked");
//	if (!waitingForResponse) {
//		console.log("IF___AnswerClicked");
//		for (var i = 0; i < 4; i++) {
//			if (answerClicked === document.getElementById("answer" + i)) {
//				sendAnswer = i;
//				window.clearInterval(animation);
//				var answer = JSON.stringify({
//					"RESPONSE" : sendAnswer
//				});
//				socket.send(answer);
//				console.log("Response in answerClicked versendet");
//				waitingForResponse = true;
//			}
//		}
//	} 
//}
//function defaultSettings() {
//	
//	bereitZumSenden = false;
//	firstPlayer = false;
//	tmpCat = false;
//	firstQuestion = true;
//	waitingForResponse = false;
//	
//	for (var i = 0; i < 6; i++) {
//		document.getElementById("playerCol" + i).innerHTML = "-";
//		document.getElementById("scoreCol" + i).innerHTML = 0;
//		document.getElementById("rows" + i).style.background = "Whitesmoke";
//	}
//
//	var catalogsTMP = document.getElementsByClassName("catalogDiv");
//	for (var j = 0; j < catalogsTMP.length; j++) {
//		document.getElementById("catalog" + j).style.background = "Whitesmoke";
//	}
//
//	var main = document.getElementById("main");
//	document.getElementById("main").innerHTML = "";
//
//	var loginForm = document.createElement("div");
//	loginForm.id = "loginForm";
//
//	main.appendChild(loginForm);
//
//	var loginButton = document.createElement("input");
//	loginButton.type = "button";
//	loginButton.value = "Login";
//	loginButton.id = "loginButton";
//	loginButton.addEventListener("click", send, false);
//
//	var userName = document.createElement("input");
//	userName.type = "text";
//	userName.name = "userName";
//	userName.id = "userName";
//
//	loginForm.appendChild(userName);
//	loginForm.appendChild(loginButton);
//}
//
//function initPlayerTable() {
//
//	var playersDiv = document.getElementById("players");
//	var playerTable = document.createElement("table");
//	playerTable.id = "playerTable";
//
//	for (var i = 0; i < 6; i++) {
//
//		var tableRow = playerTable.insertRow();
//		for (var j = 0; j < 3; j++) {
//
//			var tableCell = tableRow.insertCell();
//			if (j === 0) {
//				tableCell.innerHTML = i + 1 + ".";
//				tableCell.setAttribute("width", "20%");
//			} else if (j === 1) {
//				tableCell.id = "playerCol" + i;
//				tableCell.innerHTML = "-";
//				tableCell.className = "playerCol";
//				tableCell.setAttribute("width", "55%");
//			} else {
//				tableCell.id = "scoreCol" + i;
//				tableCell.innerHTML = "-";
//				tableCell.setAttribute("width", "25%");
//			}
//		}
//	}
//	playersDiv.appendChild(playerTable);
//}
