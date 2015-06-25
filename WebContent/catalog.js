document.addEventListener('DOMContentLoaded', init, false);

function init() {

	request = new XMLHttpRequest();
	request.open("GET", "/WebQuizSS2015/catalogs", true);
	request.onreadystatechange = handler;
	request.send();
}

function handler() {

	switch (request.readyState) {
	case 2:

		break;
	case 4:
		
		var antwort = request.responseText;

		var bla = JSON.parse(antwort);

		var catDiv = document.getElementById("catalog");
		var length = bla.length;

		for (var i = 0; i < length; i++) {

			var div = document.createElement("div");
			div.className = "catalogDiv";
			div.id = "catalog" + i;

			var divname = document.createTextNode(bla[i]);

			div.appendChild(divname);
			catDiv.appendChild(div);
		}

		break;

	default:

		break;

	}
}

function initCatListener() {

	var catDiv = document.getElementById("catalog");
	var divs = catDiv.getElementsByClassName("catalogDiv");

	for (var i = 0; i < divs.length; i++) {
		var div = document.getElementById("catalog" + i);
		div.addEventListener("click", catalogClicked, false);
	}
}

function catalogClicked(event) {
	console.log(event.target.innerHTML);
	catalogSelected(event.target.innerHTML);
	var catalogClicked = event.target;

	var catalogsTMP = document.getElementsByClassName("catalogDiv");

	for (var j = 0; j < catalogsTMP.length; j++) {

		if (catalogClicked === catalogsTMP[j]) {

			var divToChange = document.getElementById("catalog" + j);
			divToChange.style.background = "#7C907A";
		} else {

			var defaultColorDiv = document.getElementById("catalog" + j);
			defaultColorDiv.style.background = "Whitesmoke";
		}
	}
}

function setTmpCat(catName) {

	var catalogs = document.getElementsByClassName("catalogDiv");
	
	for (var j = 0; j < catalogs.length; j++) {

		var cat = document.getElementById("catalog" + j);

		if (document.getElementById("catalog" + j).innerHTML === catName) {
			
			var divToChange = document.getElementById("catalog" + j);
			divToChange.style.background = "#7C907A";
		}else{
			
			var defaultColorDiv = document.getElementById("catalog" + j);
			defaultColorDiv.style.background = "Whitesmoke";
		}
	}
}