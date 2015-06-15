package quiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
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

import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;

@ServerEndpoint("/Login")
public class Login {
	Quiz quiz;
	ScoreAgent agent;
	
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

		}
    }
}
