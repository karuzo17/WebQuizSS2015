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
	public boolean started=false;
	public boolean button = false;
	public int size=0;
	public int counter=0;
	public boolean gameover=false;
	ScoreAgent(){
		
	}
	
	public void run() {
		System.out.println("Thread gestartet ");

		while (true) {

			System.out.println("ICH laufe ");
			synchronized (this) { // Instanz von
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!gameover) {
					size = GameConnections.SessionCount();
					if (size < 2) {
						System.out.println("Button FALSE FALSE FALSE");
						button = false;
						counter = 0;
					}
					if (size == 2) {
						button = true;
						System.out.println("Buton true true true true");
					}

					//
					// while(running){
					System.out
							.println("----ScoreAgent--------ScoreAgent--------");
					JSONObject json = new JSONObject();
					try {
						json.put("PLAYERLIST", GameScores.getInstance());
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String msg = json.toString();
					// System.out.println("SessionCount"+GameConnections.SessionCount());
//					System.out.println("GAMEConnections"
//							+ GameConnections.outputAllSessions());
//					System.out.println("TMP-Connections"
//							+ GameConnections.outputAllTMPSessions());

					Map<Long, Session> map = GameConnections.getMap();

					for (Long key : map.keySet()) {
						System.out.println("map and key" + map.get(key));
						Session s = map.get(key);
						try {
							s.getBasicRemote().sendText(msg, true);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					System.out.println("Gäste LOOP"
							+ GameConnections.SessionTMPCount());
					for (int i = 0; i < GameConnections.SessionTMPCount(); i++) {
						Session s = GameConnections.getTMPSession(i);

						try {
							s.getBasicRemote().sendText(msg, true);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (GameConnections.SessionCount() == 2 && !started
							&& button && counter == 0) {
						System.out.println("Start möglich");
						Session leader = GameConnections.getSession((long) 0);
						JSONObject start = new JSONObject();
						try {
							start.put("GAMESTART", true);
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
						counter++;
					}
					System.out
							.println("ENDE----------Ende------ScoreAgent--------ScoreAgent-------ENDE-");
					running = false;

					System.out.println("Thread fertig");
				}
				
				if(gameover){
					Map<Long, Session> map = GameConnections.getMap();

					for (Long key : map.keySet()) {
						System.out.println("map and key" + map.get(key));
						Session s = map.get(key);
						JSONObject obj = new JSONObject();
						int rank = GameScores.getRank(s);
						try {
							obj.put("RANK",rank );
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							s.getBasicRemote().sendText(obj.toString());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
		}
		// }
		
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
