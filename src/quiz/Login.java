package quiz;

import java.io.IOException;
import java.util.Collection;

import javax.websocket.EndpointConfig;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;

@ServerEndpoint("/Login")
public class Login {
	Quiz quiz;
	JSONArray array;
	@OnError
	public void error(Session session, Throwable t) 
	{
	      System.out.println("Fehler beim Öffnen des Sockets");
	}
	
	@OnOpen
	// Ein Client meldet sich an und eröffnet eine neue Web-Socket-Verbindung
	public void open(Session session, 
                 EndpointConfig conf) throws JSONException 
	{
		Connections.addSession(session);
		System.out.println("neue hinzugefuegte Session:"+session);
		
		quiz=Quiz.getInstance();
		Collection<Player> players = quiz.getPlayerList();
		array = new JSONArray();
		for(Player p:players){
			JSONObject obj = new JSONObject();
			obj.put("username", p.getName());
			obj.put("score",p.getScore());
			obj.put("id", p.getId());
			array.put(obj);
		}
		String msg = array.toString();
		for ( int i=0; i < Connections.SessionCount(); i++ ) 
  	  {    Session s= Connections.getSession(i);
  	       System.out.println(s);
  		   try {
			s.getBasicRemote().sendText(msg, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  	  }
		
	}
	
	@OnMessage
    public void loginResponse(Session session, String msg, boolean last) throws JSONException 
    {
		quiz=Quiz.getInstance();
		QuizError error = new QuizError();
		Player player=quiz.createPlayer(msg, error);
		System.out.println(player.getName());
		JSONObject obj = new JSONObject();
		obj.put("username",player.getName());
		obj.put("score", player.getScore());	
		obj.put("id", player.getId());
		array.put(obj);
		
		
		msg = array.toString();
		for ( int i=0; i < Connections.SessionCount(); i++ ) 
  	  {    Session s= Connections.getSession(i);
  	       System.out.println(s);
  		   try {
			s.getBasicRemote().sendText(msg, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  	  }
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
