package quiz;

import java.io.IOException;
import java.util.ArrayList;
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

	public static final LinkedHashMap<Long,Session> socketliste = new LinkedHashMap<Long,Session>();  				// Vorsicht unsynchronisiert!!;
	public static final ArrayList<Session> tmplist= new ArrayList<Session>();
<<<<<<< HEAD
	public static final LinkedHashMap<Long,String> liste = new LinkedHashMap<Long,String>();  

	public static final ArrayList<Session> donePlayers =new ArrayList<Session>();
=======

>>>>>>> Server
	public static final Map<Long,Long> sortedHash = new LinkedHashMap<Long, Long>();
	
	
	public static synchronized void addPlayerToDonePlayers(Session value){
		donePlayers.add(value);
	}
	public static synchronized int getDonePlayersSize(){
		return donePlayers.size();
	}

	public static boolean GameMode=false;
	public static int GameOverCount=1;

	public static synchronized void resetConnections(){

//		for (Long key : socketliste.keySet()) {
//			System.out.println("map and key" + socketliste.get(key));
//			Session s = socketliste.get(key);
//			tmplist.add(s);
//		}
		tmplist.clear();
		System.out.println("Verbindungen in TMP verschoben ");
		socketliste.clear();
		System.out.println("alte Verbindungen gelöscht ");
		GameMode=false;
		GameOverCount=1;
	}
	
<<<<<<< HEAD


=======
>>>>>>> Server
	public static synchronized Map<Long, Session> getMap(){
		return socketliste;
	}

<<<<<<< HEAD
	public static synchronized void updateJSONScore(long id, long score)
			throws JSONException {
		for (int i = 0; i < array.length(); i++) {
			JSONObject json = array.getJSONObject(i);
			if (json.getLong("id") == id) {
				json.put("score", score);
			}
		}
	}
	
	public static void calcRank(){
		
		Map<Long,Long> sorted =sortByValues(sortedHash);
		int i =1;
		for(Map.Entry<Long,Long> entry : sorted.entrySet()){
			System.out.println("entrykey"+entry.getKey()+"Vaule"+entry.getValue());
			ranked.put(entry.getKey(), i);
			i++;
		}
	}

	public static synchronized int getRank(Session session){
		
		long id=getID(session);
		System.out.println("ID des Spielers"+id);
		int rank =-1 ; 
		int i=1;
		for(Long key : sortedHash.keySet()){

			System.out.println("in for");
			if(id==key){
				System.out.println("in if");
				System.out.println("RANK des Spielers " +i);
				rank = i ; 
			}
			i++;
		}
		return rank;		
	}
	
	public static synchronized void updateHighScoreList() throws JSONException{
		
		System.out.println("---------HighScore-Update-------");
		JSONArray arj = array;
		Map<Long,Long> sortedHash = new LinkedHashMap<Long, Long>();
		Map<Long,String> names = new LinkedHashMap<Long, String>();
		JSONArray sorted = new JSONArray();
		System.out.println("ARRAYJSON"+arj);
		for (int i = 0; i < arj.length(); i++) {
			JSONObject obj1 = arj.getJSONObject(i);
			long score1 = (Long) obj1.get("score");
			long id1 = (Long) obj1.get("id");
			sortedHash.put(id1,score1 );
			String name1 = (String) obj1.get("username");
			names.put(id1, name1);
		}
		Map<Long,Long> sortie =sortByValues(sortedHash);

		System.out.println("IDS-SIUZE"+ids.size());
		for(int k=0; k<ids.size();k++){
			JSONObject json = new JSONObject();
			long  id=ids.get(k);
			json.put("username",names.get(id));
			json.put("score", sortie.get(id));	
			json.put("id", id);
			sorted.put(json);
		}
		ids.clear();

		array=sorted;
	
		System.out.println("---------HighScore-Update--ENde------------");
	}

	public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
      
        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {
        	
           
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        Collections.reverse(entries);
        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K,V> sortedMap = new LinkedHashMap<K,V>();
      
        for(Map.Entry<K,V> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
            ids.add((Long) entry.getKey());
           
        }
      
        return sortedMap;
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
	
=======
>>>>>>> Server
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
