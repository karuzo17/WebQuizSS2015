package quiz;

import java.io.IOException;
import java.util.Map;

import javax.websocket.Session;

import org.json.JSONException;
import org.json.JSONObject;

public class CatalogHandler extends Thread{

	
	public static final CatalogHandler handler= new CatalogHandler();
	public String catalogName ; 
	
	public void run(){
		
		System.out.println("Handler gestartet ");
		while(true){
			
			System.out.println("Handler in while");
			synchronized (this) {
				
				System.out.println("in synched block");
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("-------GAMEHANDLE-------");
				System.out.println("Katalogname"+catalogName);
				JSONObject obj = new JSONObject();
				try {
					obj.put("CATALOG",catalogName);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String msg = obj.toString();
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
			     System.out.println("Handler fertig");
			}
		}
	}
	
	public void setCatalogName(String name){
		System.out.println("Name set to"+catalogName);
		catalogName=name;
	}
	public String getCatalogName(){ 
		return catalogName;
		
	}
	
	public static CatalogHandler getInstance(){
		
		return CatalogHandler.handler;
		
	}
}
