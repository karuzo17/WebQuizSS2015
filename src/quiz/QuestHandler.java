package quiz;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import javax.websocket.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.fhwgt.quiz.application.Player;
import de.fhwgt.quiz.application.Question;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.error.QuizError;

public class QuestHandler extends Thread {

	public static final QuestHandler quest= new QuestHandler(); 
	Quiz quiz;
	public QuestHandler() {
		// TODO Auto-generated constructor stub
	}
	
	
	public void run(){
		
		System.out.println("Quesgter gestartet");
		
		while(true){
			
			System.out.println("------QUESTER-----------QUESTER______");
			
			synchronized (this) {  //Instanz von 
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				quiz =Quiz.getInstance();
				Collection<Player> players = quiz.getPlayerList();
				 Map<Long, Session> map = GameConnections.getMap();
			     
			     for(Long key :map.keySet()){
			    	System.out.println("map and key"+ map.get(key));
			    	Session session =map.get(key);
			    	QuizError error= new QuizError();
			    	TimerTask timeoutTask = new TimerTask() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							
						}
					};
			    	for(Player p:players){
			    		
			    		if(key==p.getId()){
			    			Question quest =quiz.requestQuestion(p, timeoutTask, error);
			    			String frage = quest.getQuestion();
			    			List<String> antworten = quest.getAnswerList();
							Long timeout=quest.getTimeout();
							JSONArray arj = new JSONArray();
							arj.put(frage);
							for(String s:antworten){
								arj.put(s);
							}
							System.out.println("TimerTask."+timeoutTask.scheduledExecutionTime());
							
							arj.put(timeout);
							
							JSONObject question = new JSONObject();
							try {
								question.put("QUESTION", arj);
							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							try {
								System.out.println("Frage versendet an Player mit ID : "+ p.getId());
								session.getBasicRemote().sendText(question.toString(), true);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    		}
			    	}
			    	
			    	
			     }
			}
		}
			
			
			
			
//			}
//		}
	}
	
	
	
	public static QuestHandler getInstance(){
		return QuestHandler.quest;
	}
}
