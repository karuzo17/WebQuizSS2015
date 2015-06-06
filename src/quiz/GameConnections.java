package quiz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.Session;

public class GameConnections {

	
	public static final Map<Long,Session> socketliste = new HashMap<Long,Session>();  				// Vorsicht unsynchronisiert!!;

	// Synchronisierte Zugriffe auf die Liste
	public  static synchronized String outputAllSessions(){ 
		return socketliste.toString(); 
	}  
	
	// Verbindung an der Position i holen
    public  static synchronized Session getSession(long i) { 
    	return socketliste.get(i);
    }  
    
    // Anzahl der Verbindungen besorgen
    public  static synchronized int SessionCount() { 
    	return socketliste.size();
    }
    
    // Verbindung hinzufügen
    public  static synchronized void addSession(Session session,long ID) { 
//    	socketliste.add(session);  
    	socketliste.put(ID,session);
    }
    
    // Verbindung entfernen
    public  static synchronized void SessionRemove(Session session) { 
    	socketliste.remove(session);
    }
    
    public static synchronized void updateID(Session session,long ID){
    	
    	if(socketliste.containsValue(session)){
    		System.out.println("Füge Spieler mit ID: "+ID +"und der Session: "+session +"hinzu");
    		socketliste.put(ID, session);
    		System.out.println("Alter eintrag gelöscht");
    		System.out.println("get "+socketliste.entrySet());
    		socketliste.remove((long)-1, session);
    		System.out.println("Socketliste-Größe: "+socketliste);
    	}
    }
}
