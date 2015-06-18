package quiz;

import java.io.IOException;
import java.util.Map;

import javax.websocket.Session;

import org.json.JSONException;
import org.json.JSONObject;

import de.fhwgt.quiz.application.Game;

public class ScoreAgent extends Thread{

	private boolean running = true;
	public static final ScoreAgent agent=new ScoreAgent();
	ScoreAgent(){
		
	}
	
	public void run(){
		System.out.println("Thread gestartet ");
	

			while(true){
			
				System.out.println("ICH laufe ");
			synchronized (this) {  //Instanz von 
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			System.out.println("----ScoreAgent--------ScoreAgent--------");
			 JSONObject json = new JSONObject();
		     try {
				json.put("PLAYERLIST", GameConnections.getInstance());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		     String msg = json.toString();
		  
		     Map<Long, Session> map = GameConnections.getMap();
		     
		     for(Long key :map.keySet()){
		    	System.out.println("map and key"+ map.get(key));
		    	Session s =map.get(key);
		    	try {
					s.getBasicRemote().sendText(msg, true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		     }
		     System.out.println("Gäste LOOP"+GameConnections.SessionTMPCount());
		     for(int i=0;i<GameConnections.SessionTMPCount();i++){
		    	 Session s=GameConnections.getTMPSession(i);
		    	 
		    	 try {
					s.getBasicRemote().sendText(msg, true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		     }
		 	if(GameConnections.SessionCount()==2){
				System.out.println("Start möglich");
				Session leader =GameConnections.getSession((long)0);
				JSONObject start= new JSONObject();
				try {
					start.put("GAMESTART",true);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					leader.getBasicRemote().sendText(start.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		 	//Wenn der erste Spieler sich anmeldet, bekommt er das LEADER-JSON
		 	if(GameConnections.SessionCount()==1){
				System.out.println("!!!	Leader angemeldet	!!!");
				Session leader =GameConnections.getSession((long)0);
				JSONObject start= new JSONObject();
				try {
					start.put("LEADER",true);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					leader.getBasicRemote().sendText(start.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		     System.out.println("ENDE----------Ende------ScoreAgent--------ScoreAgent-------ENDE-");
		running = false;
			
				System.out.println("Thread fertig");
				
				}
			}
//			}
			
		
	}
	
	public static ScoreAgent getInstance(){
		return ScoreAgent.agent;
	}
	public void pause(){
		running = false;
	}
	public void restart(){
		running = true;
		notify();
	}
}
