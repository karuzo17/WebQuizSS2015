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
//	     JSONObject json = new JSONObject();
//	     json.put("Playerlist", GameConnections.getInstance());
//	     String msg = json.toString();
//	     System.out.println("new connections size "+ GameConnections.outputAllSessions());
//	     Map<Long, Session> map = GameConnections.getMap();
//	     
//	     for(Long key :map.keySet()){
//	    	System.out.println("map and key"+ map.get(key));
//	    	Session s =map.get(key);
//	    	try {
//				s.getBasicRemote().sendText(msg, true);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	     }
//			System.out.println("Gäste LOOP"+GameConnections.SessionTMPCount());
//		     for(int i=0;i<GameConnections.SessionTMPCount();i++){
//		    	 Session s=GameConnections.getTMPSession(i);
//		    	 
//		    	 try {
//					s.getBasicRemote().sendText(msg, true);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		     }
	     
	      
	}
	
	@OnOpen
	// Ein Client meldet sich an und eröffnet eine neue Web-Socket-Verbindung
	public void open(Session session, 
                 EndpointConfig conf) throws JSONException 
	{
		System.out.println("-----------------------OnOpen--------------------OnOpen--------------");
	
//		synchronized (agent) {
//			agent.restart();
//		}
		GameConnections.addTMPSession(session);
//		GameConnections.addSession(session, -1);
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
		
//		quiz=Quiz.getInstance();
//		Collection<Player> players = quiz.getPlayerList();
//		System.out.println("PLAYERSIZE"+quiz.getPlayerList().size());
//		for(Player p:players){
//			JSONObject obj = new JSONObject();
//			obj.put("username", p.getName());
//			obj.put("score",p.getScore());
//			obj.put("id", p.getId());
//			System.out.println("vor put on open");
//			GameConnections.addObject(obj);
////			array.put(obj);
//		}
//		System.out.println("ARRRAYJSONONOPEN"+GameConnections.getInstance());
		
//		System.out.println("dämon?"+agent.isDaemon());
//		
//		agent = ScoreAgent.getInstance();
//		if(!agent.isAlive()){
//			agent.start();
//		}else{
//			agent.restart();
//		}
//		synchronized (agent) {
//			agent.pause();
//		}

		
//		agent = new ScoreAgent();
//		agent.start();
		JSONObject json = new JSONObject();
		json.put("Playerlist",GameConnections.getInstance());
		String msg = json.toString();
		
		
		
//			for ( int i=0; i < GameConnections.SessionTMPCount(); i++ ) 
//	  	  {    
//			
//			Session s= GameConnections.getTMPSession(i);
//	  	    System.out.println(s);
//	  		   try {
//				s.getBasicRemote().sendText(msg, true);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	  	  }
		
		
	}
	
	@OnMessage
    public void loginResponse(Session session, String msg, boolean last) throws JSONException 
    {
		System.out.println("----OnMessage---OnMessage---OnMessage----OnMessage-----");
//		agent = ScoreAgent.getInstance();
//		synchronized (agent) {
//			agent.terminate();
//		}
			
			
		JSONObject blub = new JSONObject(msg);
		if(blub.keys().next().equals("Abmelden")){
			System.out.println("Spieler mit Session"+session+"hat sich abgemeldet");
		}
		
		if(blub.keys().next().equals("newPlayer")){
			System.out.println("NewPlayer detected");
			
		
		System.out.println("BLUB"+blub.getString("newPlayer"));
		System.out.println("BLUB"+blub.has("newPlayer"));
//		String str = blub.getString("newPlayer");
//		System.out.println("String Name"+str);
//		System.out.println(blub.));
		
		quiz=Quiz.getInstance();
		QuizError error = new QuizError();
		Player player=quiz.createPlayer(blub.getString("newPlayer"), error);
		System.out.println(player.getName());
		JSONObject obj = new JSONObject();
		obj.put("username",player.getName());
		obj.put("score", player.getScore());	
		obj.put("id", player.getId());
		GameConnections.addJSONObject(obj);
//		array.put(obj);
		
		
		
//		JSONObject json = new JSONObject();
//		System.out.println("ARRRAYJSONONMESSAGE"+GameConnections.getInstance());
//		json.put("Playerlist", GameConnections.getInstance());
		GameConnections.addSession(session, player.getId());
		
		
		ScoreAgent agent= ScoreAgent.getInstance();
		if(!agent.isAlive()){
			agent.start();
		}else{
			
			synchronized (agent) {
				agent.restart();
			}
		}
//		msg = json.toString();
//		Map<Long, Session> map = GameConnections.getMap();
//	     
//	     for(Long key :map.keySet()){
//	    	System.out.println("map and key"+ map.get(key));
//	    	Session s =map.get(key);
//	    	try {
//				s.getBasicRemote().sendText(msg, true);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	     }
//		System.out.println("Gäste LOOP"+GameConnections.SessionTMPCount());
//	     for(int i=0;i<GameConnections.SessionTMPCount();i++){
//	    	 Session s=GameConnections.getTMPSession(i);
//	    	 
//	    	 try {
//				s.getBasicRemote().sendText(msg, true);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	     }
	
//		Collection<Player> players = quiz.getPlayerList();
//		JSONArray array = new JSONArray();
//		for(Player p:players){
//			JSONObject obj = new JSONObject();
//			obj.put("username", p.getName());
//			obj.put("score",p.getScore());
//			
//			array.put(obj);
//		}
//		System.out.println("Antwort gesendet");
//		String name = msg;
//		System.out.println("Name:"+name);
//		msg=array.toString();
//		 if (session.isOpen()) {
//             try {
//				session.getBasicRemote().sendText(msg, last);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//         }
		}
    }
}
