package quiz;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;









import org.jdom2.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.fhwgt.quiz.application.Catalog;
import de.fhwgt.quiz.application.Question;
import de.fhwgt.quiz.application.Quiz;
import de.fhwgt.quiz.loader.FilesystemLoader;
import de.fhwgt.quiz.loader.LoaderException;

/**
 * Servlet implementation class CatalogServlet
 */
@WebServlet("/CatalogServlet")
public class CatalogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    Quiz quiz;
    List<Question> questions ;
    Question question;
 /**
  * @see HttpServlet#HttpServlet()
  */
 public CatalogServlet() {
     super();
     System.out.println("catServlet");
//     FilesystemLoader loader = new FilesystemLoader("/Users/derLu/WebProg/EclipseWorkspace/WebQuizSS2015/WebContent/WEB-INF/Kataloge");
     FilesystemLoader loader = new FilesystemLoader("/home/PRJ/svn/webprog/g06/www/tomcat/WEB-INF/Kataloge");
//     FilesystemLoader loader = new FilesystemLoader("/Users/TIm/Downloads/WebQuiz2015/WebContent/WEB-INF/Kataloge");
     quiz = Quiz.getInstance();
     quiz.initCatalogLoader(loader);

//     try {
//			Catalog cat = (Catalog)quiz.getCatalogByName("XMLsimple.xml");
//			
//			questions= cat.getQuestions();
//			for (int i =0; i<questions.size();i++){
////				System.out.println("Fragetext: "+ questions.get(i).getQuestion().toString());
////				System.out.println("Timeout: " + questions.get(i).getTimeout());
//				List<String> answers;
//				for(int j =0; j<4;j++){
//					answers = questions.get(j).getAnswerList();
//				
//				}
//			}
//			
//		} catch (LoaderException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//     
//     // TODO Auto-generated constructor stub
}

 public void init(ServletConfig config) throws ServletException {   
 	super.init(config);
 } 
 
 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 	
 	System.out.println("HTTP-Request");
 
 	Map<String, Catalog> catalogList;
// 	JSONObject catalogs = new JSONObject();
 	JSONArray catalogs = new JSONArray();
 
 			try {
			catalogList = quiz.getCatalogList();
			

			System.out.println(catalogList);
			System.out.println("CatSize:"+catalogList.size());
			for (Map.Entry<String, Catalog> e : catalogList.entrySet()) {
				String name=e.getValue().getName();
//				String cat="catalogs"+i;
				catalogs.put(name);
				
			
			}
//			catalogs.put("size", i);
//			catalogs.put("catalogs", "xmlundso");
//			catalogs.put("Catalogs", catalogList);
			String json =catalogs.toString();
			response.setContentType("application/json");
	    	PrintWriter writer = response.getWriter();
	    	writer.print(json);
			
		} catch (LoaderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
 	
 
 	
 }
 
 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 	
 }
}