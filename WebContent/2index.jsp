<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, java.text.*" %>
<%@ page import="de.fhwgt.quiz.application.*" %>
<%@ page import="de.fhwgt.quiz.loader.FilesystemLoader" %>
<%@ page import="quiz.LoginServlet" %>
<%@ page import="quiz.CatalogServlet" %>
<!DOCTYPE html>
<html>
<head>
<script type="text/javascript" src="js/javaScriptLoad.js"></script>
<script type="text/javascript" src="js/javaScriptLogin.js"></script>
<script type="text/javascript" src="js/javaScriptCatalogs.js"></script>    
<script type="text/javascript" src="js/javaScriptHighscore.js"></script>
<link rel="stylesheet" type="text/css" href="index.css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>WebQuiz</title>
<link href='http://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet' type='text/css'>
</head>
<body >
	<%  Quiz quiz;
		
		if ( session == null){
			System.out.print("session null");
			System.out.print(session.getId().toString()+"\n");
		
        	quiz = Quiz.getInstance();
        	session.setAttribute("quiz", quiz);
		}else{
			System.out.print("session else");
			System.out.print(session.getId().toString()+"\n");
			quiz = (Quiz) session.getAttribute("quiz");
	
		}	
	%>
	
	<div id="frame">
		<Header id="header"><img src="quiz.png" alt="Hier fehlt was!" width="25" height="25" id="titleIMG"><span id="titleSpan">WebQuiz</span></Header>
		
		<Section id="main">
			<div id="login">
				<form id="loginForm" method="get" action="/WebQuiz2015/login">
					<input type="text" id="userName" name="userName"> <br>
					<input type="submit" id="loginButton" value="Login">
				</form>
			</div>
		</Section>

		<%@include file="catalog.jsp" %>
		
		<%@include file="highscore.jsp" %>
		
		<Footer id="footer">
			© 2015 by Daniel Lutschinski, Tim Härle
		</Footer>
	</div>
</body>
</html>