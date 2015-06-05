package quiz;
import java.util.ArrayList;
import javax.websocket.Session;


public class Connections {

	public static final ArrayList<Session> socketliste = new ArrayList<Session>();  				// Vorsicht unsynchronisiert!!;

	// Synchronisierte Zugriffe auf die Liste
	public  static synchronized String outputAllSessions(){ 
		return socketliste.toString(); 
	}  
	
	// Verbindung an der Position i holen
    public  static synchronized Session getSession(int i) { 
    	return socketliste.get(i);
    }  
    
    // Anzahl der Verbindungen besorgen
    public  static synchronized int SessionCount() { 
    	return socketliste.size();
    }
    
    // Verbindung hinzufügen
    public  static synchronized void addSession(Session session) { 
    	socketliste.add(session);    
    }
    
    // Verbindung entfernen
    public  static synchronized void SessionRemove(Session session) { 
    	socketliste.remove(session);
    }
}
