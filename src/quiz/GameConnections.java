package quiz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.text.html.HTMLDocument.Iterator;
import javax.websocket.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.swing.internal.plaf.synth.resources.synth;

import de.fhwgt.quiz.application.Player;

public class GameConnections {

	public static  JSONArray array = new JSONArray();
	public static ArrayList<Long> ids = new ArrayList<Long>();
	public static final LinkedHashMap<Long,Session> socketliste = new LinkedHashMap<Long,Session>();  				// Vorsicht unsynchronisiert!!;
	public static final ArrayList<Session> tmplist= new ArrayList<Session>();

	public static final Map<Long,Long> sortedHash = new LinkedHashMap<Long, Long>();
	public static boolean GameMode=false;
	private static boolean isCalculated=false;
	private static Map<Long,Integer> ranked = new HashMap<Long, Integer>();
	


	public static synchronized Map<Long, Session> getMap(){
		return socketliste;
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
    

    public static synchronized boolean isPlayer(Session s){
    	long id =getID(s);
    	boolean player=false;
    	
    	java.util.Iterator<Entry<Long, Session>> iter = socketliste.entrySet().iterator();

    	while(iter.hasNext()){
    		Map.Entry<Long, Session> entry = iter.next();
    		if(entry.getValue().equals(s)){
    			player=true;
    		}
    	}
    	
    	System.out.println("containsValue"+player);
    	return player;
    	
    }
}
