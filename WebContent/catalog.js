/**
 * 
 */

function init(){
	
	alert("ALARM");
	request= new XMLHttpRequest();
	request.open("GET","/WebQuiz2015/catalogs", true);
	request.onreadystatechange=handler;
	request.send();
}

function handler(){
	
	
	switch (request.readyState) {
	case 2:

		break;
	case 4:

		var antwort = request.responseText;
		
		
		var bla= JSON.parse(antwort);

		
		var catDiv = document.getElementById("catalog");
		var length=bla.length;
		
	for(var i=0;i<length;i++){
		
		var div=document.createElement("div");
		div.className="catalogDiv";
		div.id="catalog"+i;
		
		var divname = document.createTextNode(bla[i]);
		
		div.appendChild(divname);
		div.addEventListener("click",catalogClicked,false);
		catDiv.appendChild(div);
		
	}

		break;

	default:
		
		break;
	
	}
}
//dickes Problem 
function catalogClicked(event){
	var catalogClicked = event.target;
    var catalogsTMP = document.getElementsByClassName("catalogDiv");
    alert(catalogsTMP.length);
    alert(catalogClicked.id);
    for(var j = 0; j < catalogsTMP.length; j++){
    	alert(catalogClicked.id +" "+catalogsTMP[j].id);
        if(catalogClicked === catalogsTMP[j]){
        	alert("grün");
            var divToChange = document.getElementById("catalog"+j);
            divToChange.style.background = "#7C907A";
        }
        else{
        	alert("grau");
            var defaultColorDiv = document.getElementById("catalog"+j);
            defaultColorDiv.style.background = "Whitesmoke";
        }
    }
}