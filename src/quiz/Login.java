package quiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.ErrorManager;

import javax.websocket.EndpointConfig;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import jdk.nashorn.internal.parser.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.org.apache.bcel.internal.generic.NEW;

import de.fhwgt.quiz.application.Game;
import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.application.Question;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;
import de.fhwgt.quiz.loader.LoaderException;

@ServerEndpoint("/Login")
public class Login {
	Quiz quiz;
	Game game;
	ScoreAgent agent;
	GameHandler handler;
	String tmpCatalog;
	Question quest;
	Player leader=null;
	
	public Login(){
	
//		agent = ScoreAgent.getInstance();
//		System.out.println("KONSTRuktor von LOGIN");
//		System.out.println("lebt der agent?"+agent.isAlive());
//		if(agent.isAlive()){
//			System.out.println("agent restart dasdasijfidshfuidshfukjdshfkjdshfjksdhfkjs");
//			agent.restart();
//		}else{
//		agent = ScoreAgent.getInstance();
//		agent.start();
//		}
	}
	
	
	@OnError
	public void error(Session session, Throwable t) throws JSONException 
	{
		System.out.println("FEHLER FEHLER FEHLER FEHLER ");
		System.out.println("FEHLER FEHLER FEHLER FEHLER ");
		System.out.println("FEHLER FEHLER FEHLER FEHLER ");
		System.out.println("GRUND"+t.getMessage());
		GameConnections.getID(session);
	      System.out.println("Fehler beim Öffnen des Sockets"+session);
	      boolean visitor = GameConnections.isPlayer(session);
	      System.out.println("on error flag "+visitor);
	      if(!GameConnections.isPlayer(session)){
	    	  System.out.println("Besucher gelöscht");
	    	  GameConnections.SessionTMPRemove(session);
	    	  ScoreAgent agent= ScoreAgent.getInstance();
				if(!agent.isAlive()){
					agent.start();
				}else{
					
					synchronized (agent) {
						agent.restart();
					}
				}
		      
	      }else{
	    	  System.out.println("Spieler gelöscht");
	     long id= GameConnections.getID(session);
	     System.out.println("ID des Spielers"+id);
	     GameConnections.IDRemove(id);
	     JSONObject obj = new JSONObject();
	     System.out.println("ARRRAYJSON"+GameConnections.getInstance());
	     GameConnections.removeJSONObject(id);
	    	
	     
	   
	     quiz=Quiz.getInstance();
	     Collection<Player> players = quiz.getPlayerList();
	     QuizError error = new QuizError();
			for(Player p:players){
				if(p.getId()==id){
					System.out.println("SPieler gelöscht");
					quiz.removePlayer(p, error);
				}
			}
	     
	     System.out.println("new array "+GameConnections.getInstance());
	     
	     ScoreAgent agent= ScoreAgent.getInstance();
//			agent.start();
	     if(!agent.isAlive()){
	    	 System.out.println("agent alive ? "+agent.isAlive());
				agent.start();
			}else{
				
				synchronized (agent) {
					agent.restart();
				}
			}
	      }

	     
	      
	}
	
	@OnOpen
	// Ein Client meldet sich an und eröffnet eine neue Web-Socket-Verbindung
	public void open(Session session, 
                 EndpointConfig conf) throws JSONException 
	{
		System.out.println("-----------------------OnOpen--------------------OnOpen--------------");
	

		GameConnections.addTMPSession(session);

		System.out.println("neue hinzugefuegte Session:"+session);
		
		ScoreAgent agent= ScoreAgent.getInstance();
		if(!agent.isAlive()){
			agent.start();
			System.out.println("Agent started");
		}else{
			
			synchronized (agent) {
				agent.restart();
			}
		}
		

		JSONObject json = new JSONObject();
		json.put("PLAYERLIST",GameConnections.getInstance());
		String msg = json.toString();
		
		
	}
	
	@OnMessage
    public void loginResponse(Session session, String msg, boolean last) throws JSONException 
    {
		System.out.println("----OnMessage---OnMessage---OnMessage----OnMessage-----");
		
			
		JSONObject blub = new JSONObject(msg);
		
		if(blub.keys().next().equals("CATALOG")){
			System.out.println("Neuer Katalog erhalten:"+blub.getString("CATALOG"));
			
			handler = GameHandler.getInstance();
			
				
				if(!handler.isAlive()){		
					System.out.println("Handler lebt nicht ");
					handler.setName(blub.getString("CATALOG"));
					handler.start();

				}
				
				Collection<Player> players= quiz.getPlayerList();
			
				for(Player p: players){
					if(p.isSuperuser()){
						leader =p;
					}
				}
				QuizError error = new QuizError();
				synchronized (handler) {
				if(handler.isAlive()){
					System.out.println("Handler lebt noch");
					handler.catalogName=blub.getString("CATALOG");
					tmpCatalog=blub.getString("CATALOG");
					quiz.changeCatalog(leader, tmpCatalog, error); //
//					handler.setName(blub.getString("CATALOG"));
					handler.notify();
					
				}
				
			}
			
		}
		if(blub.keys().next().equals("GAMESTART")){
			System.out.println("SPIEL SOLL GESTARTET WERDEN------!!!!!------");
			Collection<Player> players = quiz.getPlayerList();
		     QuizError error = new QuizError();
		  System.out.println("vor schleufe");
		  game = new Game();
				for(Player p:players){
					System.out.println("in schleufe");
					if(p.isSuperuser()){
						
						quiz.startGame(p, error);
//						System.out.println("ErrorDescription"+error.getDescription());
					}
					game.addPlayer(p, error);
				}
				System.out.println("NACH schleife");
				TimerTask task = new TimerTask() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
					}
				};
				quest =quiz.requestQuestion(leader, task, error);
				System.out.println("Nach request");
				Session leader =GameConnections.getSession((long)0);
				String frage=quest.getQuestion();
				List<String> antworten = quest.getAnswerList();
				Long timeout=quest.getTimeout();
				JSONArray arj = new JSONArray();
				arj.put(frage);
				for(String s:antworten){
					arj.put(s);
				}
				arj.put(timeout);
				
				JSONObject question = new JSONObject();
				question.put("QUESTION", arj);
				try {
					leader.getBasicRemote().sendText(question.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			System.out.println("Players in start:"+game.getPlayers());
			System.out.println("Catalog in Game:"+game.getCatalog());
			
		}
		if(blub.keys().next().equals("LOGOUT")){
			System.out.println("Spieler mit Session"+session+"hat sich abgemeldet");
		}
		
		if(blub.keys().next().equals("NEWPLAYER")){
			System.out.println("NewPlayer detected");
			
		
		System.out.println("BLUB"+blub.getString("NEWPLAYER"));
		System.out.println("BLUB"+blub.has("NEWPLAYER"));

		
		quiz=Quiz.getInstance();
		QuizError error = new QuizError();
		Player player=quiz.createPlayer(blub.getString("NEWPLAYER"), error);
		System.out.println(player.getName());
		JSONObject obj = new JSONObject();
		obj.put("username",player.getName());
		obj.put("score", player.getScore());	
		obj.put("id", player.getId());
		GameConnections.addJSONObject(obj);

		GameConnections.addSession(session, player.getId());
		
		
		ScoreAgent agent= ScoreAgent.getInstance();
		if(!agent.isAlive()){
			agent.start();
		}else{
			
			synchronized (agent) {
				agent.restart();
			}
		}
		handler = GameHandler.getInstance();
		
		
		if(!handler.isAlive()){		
			System.out.println("Handler lebt nicht ");
//			handler.setName(blub.getString("CATALOG"));
			handler.start();

		}

		}
    }
}
