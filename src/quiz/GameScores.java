package quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.websocket.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameScores {

	public static  JSONArray array;
	public static ArrayList<Long> ids = new ArrayList<Long>();
	public static final Map<Long,Long> sortedHash = new LinkedHashMap<Long, Long>();
	public static Map<Long,Long> tmpHash = new LinkedHashMap<Long, Long>();
	private static Map<Long,Integer> ranked = new HashMap<Long, Integer>();
	private static boolean isCalculated=false;
	
	public static synchronized void addJSONObject(JSONObject obj){
		if(array==null){
			System.out.println("Array neu initialisiert");
			array= new JSONArray();
		}
		System.out.println("gameJSON"+array);
		array.put(obj);
//		System.out.println("gameJSON danach"+array);
	}
	public static synchronized void resetScore(){
		array = null;
		System.out.println("Array nach reset"+array);
		
		ranked.clear();
		System.out.println("Ranked Cleared");
		ids.clear();
		isCalculated=false;
	}

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
		Map<Long,Long> sorted =sortByValues(tmpHash);
		int i =1;
		for(Map.Entry<Long,Long> entry : sorted.entrySet()){
			System.out.println("entrykey"+entry.getKey()+"Vaule"+entry.getValue());
			ranked.put(entry.getKey(), i);
			i++;
		}
	}
	public static synchronized int getRank(Session session){
		//rank ist noch buggy
		if(!isCalculated){
			calcRank();
			isCalculated=true;
		}
		long id=GameConnections.getID(session);
		System.out.println("ID des Spielers"+id);
		int rank=-1;
		for(Long key : ranked.keySet()){
			System.out.println(ranked.get(key));
			System.out.println("id des spielers"+id);
			System.out.println("in for"+key);
			if(id==key){
				System.out.println("in if");
				
				rank = ranked.get(key);
			}
			
			
		}
		return rank;
		
	}
	public static synchronized void updateHighScoreList() throws JSONException{
		
		System.out.println("---------HighScore-Update-------");
		JSONArray arj = array;
		System.out.println("IDS-SIZE"+ids.size());
		Map<Long,String> names = new LinkedHashMap<Long, String>();
		JSONArray sorted = new JSONArray();
		System.out.println("ARRAYJSON"+arj);
		System.out.println("REAL Array"+array);
		for (int i = 0; i < arj.length(); i++) {
			JSONObject obj1 = arj.getJSONObject(i);
			long score1 = (Long) obj1.get("score");
			long id1 = (Long) obj1.get("id");
			sortedHash.put(id1,score1 );
			String name1 = (String) obj1.get("username");
			names.put(id1, name1);
		}
		System.out.println("Sorted-Hash:"+sortedHash.size());
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
		
		tmpHash.clear();
		tmpHash.putAll(sortedHash);
		sortedHash.clear();
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
}
