package quiz;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import de.fhwgt.quiz.error.*;
import de.fhwgt.quiz.application.Quiz;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        System.out.println("Aufruf von LoginServlet konstruktor\n");
    }
    
    public void init(ServletConfig config) throws ServletException {   
    	super.init(config);
        System.out.println("Aufruf von LoginServlet init\n");
    } 

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Aufruf von LoginServlet doGet\n");
		System.out.println("RESPONSE"+response);
		String userName = request.getParameter("userName");
		
        HttpSession session = request.getSession(true);
        Quiz quiz;
        
        // Session gibt es schon 
        if (session != null) {   
        	quiz = (Quiz)session.getAttribute("quiz");
             
            if (quiz == null) { 
            	quiz = Quiz.getInstance();
            	session.setAttribute("quiz", quiz);
            }
        }
         
        if (session.isNew()) {  
        	quiz = Quiz.getInstance();
            
        	if (quiz != null) { 
        		session.setAttribute("quiz", quiz);
        	}
        } 

        quiz = (Quiz)session.getAttribute("quiz");
        QuizError error = new QuizError();
        quiz.createPlayer(userName, error);
        RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
        dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Aufruf von LoginServlet doPost\n");
		doGet(request, response);
	}

}
