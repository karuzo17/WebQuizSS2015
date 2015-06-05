document.addEventListener('DOMContentLoaded', initLogin, false);
 
 var socket;
 var bereitZumSenden=false;
 
 function initLogin()
 {  // Listener registrieren fÃ¼r Buttons
	var login= window.document.getElementById("loginButton");   
	login.addEventListener("click",send,false); 

	//var url = "ws://" + document.location.host  + "/Echo";
	var url = 'ws://localhost:8080/WebQuizSS15/Login';
	alert("url= "+url);
	
	socket=new WebSocket(url);

	socket.onopen=sendenMoeglich;
	socket.onclose=Closing;
	socket.onerror=ErrorHandler;
	socket.onmessage=empfange;
 }

 
// Listener fÃ¼r den Button go
 function send(event)
 {  
 	var button = event.target;
 	var outmessage=window.document.getElementById("userName").value; 
 	if ( bereitZumSenden == true)
 	    { alert("Jetzt senden");socket.send(outmessage);}
 	else alert("Server noch nicht bereit zum Empfangen. Bitte nochmals versuchen");
	 
	
 } 
 
 function sendenMoeglich(){ bereitZumSenden=true; alert("Senden ist möglich");}
 function ErrorHandler(event){ alert("Fehler bei den Websockets "+event.data);}
 function Closing(event){ alert("Websockets closing "+event.code);}
 
 function empfange(message)
 { 
	 var text = message.data;
	 alert(text);
	 var json=JSON.parse(text);
	alert("json:"+json);
	 alert(json[0].username);
	 var catDiv = document.getElementById("score");
	 for(var i=0;i<json.length;i++){
		 var div=document.createElement("div");
			div.className="playerDiv";
			div.id="player"+i;
			
			var divname = document.createTextNode(json[i].username);
			
			div.appendChild(divname);
			div.addEventListener("click",catalogClicked,false);
			catDiv.appendChild(div);
		 
	 }
// 	var text = message.data;
// 	//alert("emfange Daten vom Server: "+text);
// 	// Textfeld zur Ausgabe des Echos vom Server
// 	var output=window.document.getElementById("ausgabetext");
// 	// Bereich zur Darstellung aller angemeldeten Clients
// 	var session=window.document.getElementById("sessionID"); 
// 	
// 	// es kommt ein JSON-Array mit der Liste aller Clients
// 	if (text.charAt(0) == "[")
// 	{       // JSON-String in ein Array wandeln
// 		    var clientliste = JSON.parse(text);
// 		    
// 		    output="";
// 		    // Liste aller Clients anzeigen
// 	        for ( i=0; i<clientliste.length; i++)  output = output+"client "+i+":"+clientliste[i]+"<br/>";
// 	            session.innerHTML=output;
//    }
// 	// es kommt ein Echo 
// 	else 	output.value=text;
	
 } 