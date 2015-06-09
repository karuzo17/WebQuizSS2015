package quiz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.text.html.HTMLDocument.Iterator;
import javax.websocket.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameConnections {

	public static final JSONArray array = new JSONArray();
	
	public static final LinkedHashMap<Long,Session> socketliste = new LinkedHashMap<Long,Session>();  				// Vorsicht unsynchronisiert!!;
	public static final ArrayList<Session> tmplist= new ArrayList<Session>();
	public static final LinkedHashMap<Long,String> liste = new LinkedHashMap<Long,String>();  
	
	public static synchronized Map<Long, Session> getMap(){
		return socketliste;
	}
	// Synchronisierte Zugriffe auf die Liste
	public  static synchronized String outputAllSessions(){ 
		return socketliste.toString(); 
	}  
	public  static synchronized String outputAllTMPSessions(){ 
		return tmplist.toString(); 
	}  
	public static synchronized void addJSONObject(JSONObject obj){
		System.out.println("gameJSON"+array);
		array.put(obj);
//		System.out.println("gameJSON danach"+array);
	}
	public static synchronized JSONArray getInstance(){
		return array;
	}
	public static synchronized void removeJSONObject(long id) throws JSONException{
		
		for(int i=0;i<array.length();i++){
			JSONObject json =array.getJSONObject(i);
			if(json.getLong("id")==id){
				System.out.println("Spieler gefunden an LOOPStelle: "+i);
				array.remove(i);
				System.out.println("Array nach löschen"+array);
			}
			
		}
		
	}
	public static synchronized int getJSONArrayCount(){
		return array.length();
	}
	
	// Verbindung an der Position i holen
    public  static synchronized Session getSession(long i) { 
    	
    	return socketliste.get(i);
    	
    }  
    public  static synchronized Session getTMPSession(int i) { 
    	return tmplist.get(i);
    }  
    
    public  static synchronized long getID(Session session) { 
    	long id=0;
    	java.util.Iterator<Entry<Long, Session>> iter = socketliste.entrySet().iterator();
//    	for(Entry<Long, Session> entry:socketliste.entrySet()){
//    		System.out.println("Drin");
//    		if(entry.getValue().equals(session)){
//    			id = entry.getKey();
//    		}
//    	}
//    	for(Long key:socketliste.keySet()){
//    		Session s=socketliste.get(key);
//    		System.out.println(s);
//    		if(s==session){
//    			System.out.println("identische sessions");
//    		}
//    	}
    	while(iter.hasNext()){
    		Map.Entry<Long, Session> entry = iter.next();
    		if(entry.getValue().equals(session)){
    			id = entry.getKey();
    		}
    	}
    	return id;
    }
    
    // Anzahl der Verbindungen besorgen
    public  static synchronized int SessionCount() { 
    	return socketliste.size();
    }
    public  static synchronized int SessionTMPCount() { 
    	return tmplist.size();
    }
    
    // Verbindung hinzufügen
    public  static synchronized void addSession(Session session,long ID) { 
//    	socketliste.add(session);
    	
    
    	tmplist.remove(session);
    	System.out.println("TMPLIST nach remove"+tmplist);
    	socketliste.put(ID,session);
    	System.out.println("nach put in Socketliste");
    }
    public  static synchronized void addTMPSession(Session session) { 
//    	socketliste.add(session);  
    	tmplist.add(session);
    	System.out.println("tmp-list"+tmplist);
    }
    
    // Verbindung entfernen
    public  static synchronized void IDRemove(long id) { 
    	socketliste.remove(id);
    	System.out.println("new socketliste after remove:"+socketliste);
    }
    public  static synchronized void SessionTMPRemove(Session session) { 
    	tmplist.remove(session);
    }
    
    public static synchronized void updateID(Session session,long ID){
    	
//    	if(tmplist.contains(session)){
//    		socketliste.put(ID, session);
//    		tmplist.remove(session);
//    	}
    	if(socketliste.containsValue(session)){
    		System.out.println("Füge Spieler mit ID: "+ID +"und der Session: "+session +"hinzu");
    		socketliste.put(ID, session);
    		System.out.println("Alter eintrag gelöscht");
    		System.out.println("get "+socketliste.entrySet());
    		socketliste.remove((long)-1, session);
    		System.out.println("Socketliste-Größe: "+socketliste);
    	}
    }
    public static synchronized boolean isPlayer(Session s){
    	boolean player=socketliste.containsValue(s);
    	
    	return player;
    }
}
