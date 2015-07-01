//Event-Listener, der angesprungen wird sobald das Spiel/das Dokument geladen wurde
document.addEventListener('DOMContentLoaded', init, false);

//Initialisierung per AJAX
function init() {

	//neuer Request wird erstellt
	request = new XMLHttpRequest();
	
	//"methode" : Anfrage von Daten
	//"pfad" : relativ oder absolut
	//"flag" : true entspricht asynchronen Request
	request.open("GET", "/WebQuizSS15/catalogs", true);
	
	//Registrierung vom Handler
	request.onreadystatechange = handler;
	
	//übermittelt Request, optional kann String übergeben werden
	request.send();
}

//im Case 4 werden die vom Server empfangenen Kataloge ausgelesen
function handler() {

	switch (request.readyState) {
	
	//case "loaded"
	case 2:
		break;
	//case "complete"
	case 4:
		
		//auslesen der Antwort des Servers
		var antwort = request.responseText;
		
		//parsen von JSON-Object in JavaScript-Objekt
		var bla = JSON.parse(antwort);
		
		var catDiv = document.getElementById("catalog");
		
		//auslesen der Anzahl der Kataloge
		var length = bla.length;

		//für entsprechende Anzahl werden divs erstellt, die als Text die
		//Katalognamen erhalten
		for (var i = 0; i < length; i++) {

			var div = document.createElement("div");
			div.className = "catalogDiv";
			div.id = "catalog" + i;

			var divname = document.createTextNode(bla[i]);

			//hinzufügen der Katalog-divs in die "catalog"-Section
			div.appendChild(divname);
			catDiv.appendChild(div);
		}
		break;
	default:
		break;
	}
}

//auschließlich der Spielleiter erhält die Möglichkeit Kataloge auszuwählen (diese Fkt aufzurufen)
function initCatListener() {
	
	console.log("Init Cat Listener");
	var catDiv = document.getElementById("catalog");
	var divs = catDiv.getElementsByClassName("catalogDiv");

	//alle Kataloge bekommen einen Eventlistener, damit der Spielleiter einen auswählen kann
	for (var i = 0; i < divs.length; i++) {
		var div = document.getElementById("catalog" + i);
		div.addEventListener("click", catalogClicked, false);
	}
}

//damit nach Neustart eines Spiels kein früherer Spieler jetzt Spielleiter wird und somit zwei
//Spieler Kataloge auswählen können, werden die Listener nach dem Spielstart wieder entfernt
function removeCatListener() {
	
	console.log("remove Cat Listener");
	var catDiv = document.getElementById("catalog");
	var divs = catDiv.getElementsByClassName("catalogDiv");

	//entfernen aller Eventlistener
	for (var i = 0; i < divs.length; i++) {
		var div = document.getElementById("catalog" + i);
		div.removeEventListener("click", catalogClicked, false);
	}
}

//Nur für Spielleiter
//Funktion hinterlegt den Katalog, den der Spielleiter ausgewählt hat, GRÜN
function catalogClicked(event) {
	
	console.log(event.target.innerHTML);
	
	//Spielleiter sendet den ausgewählten Katalog an der Server
	catalogSelected(event.target.innerHTML);
	
	//speichert hier den ausgewählten Katalog...
	var catalogClicked = event.target;

	//... und besorgt sich alle vorhandenen Kataloge ...
	var catalogsTMP = document.getElementsByClassName("catalogDiv");

	//... damit unter allen vorhandenen Katalogen derjenige identifiziert werden kann,
	//der angeklickt wurde
	for (var j = 0; j < catalogsTMP.length; j++) {

		//... um ihn in diesem Falle GRÜN zu hinterlegen ...
		if (catalogClicked === catalogsTMP[j]) {
			var divToChange = document.getElementById("catalog" + j);
			divToChange.style.background = "#7C907A";
		
		// ... und im anderen Fall auf den default-Wert ("Whitesmoke") zu setzen
		} else {
			var defaultColorDiv = document.getElementById("catalog" + j);
			defaultColorDiv.style.background = "Whitesmoke";
		}
	}
}

//Nur "normale" Spieler
//hinterlegt den vom Spielleiter ausgewählten Katalog GRÜN...
function setTmpCat(catName) {

	//besorgen aller Kataloge
	var catalogs = document.getElementsByClassName("catalogDiv");
	
	for (var j = 0; j < catalogs.length; j++) {

		//den angeklickten finden und GRÜN hinterlegen ...
		if (document.getElementById("catalog" + j).innerHTML === catName) {
			
			var divToChange = document.getElementById("catalog" + j);
			divToChange.style.background = "#7C907A";
			
		//oder aber default-Wert ("Whitesmoke") als Hintergrund setzten
		}else{
			var defaultColorDiv = document.getElementById("catalog" + j);
			defaultColorDiv.style.background = "Whitesmoke";
		}
	}
}
