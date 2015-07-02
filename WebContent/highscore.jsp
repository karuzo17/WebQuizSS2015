<div id="score">
			<h2><img src="cup.svg" alt="Hier fehlt was!" width="25" height="25" class="title">Highscore</h2>
			<hr class="line"/>
        	<% 
        	
        	if(quiz!=null){
        		Collection <Player> player = quiz.getPlayerList(); 
        	 	for(Iterator<Player> iterator=player.iterator();iterator.hasNext();){%>
        			<div class="playerDiv"><%= iterator.next().getName() %></div>
        			<%	
        		}
        	 } %>
		</div>