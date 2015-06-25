package de.fhwgt.quiz.loader;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;





//import org.jdom2.Document;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;

import de.fhwgt.quiz.application.Catalog;
import de.fhwgt.quiz.application.Question;

public class FilesystemLoader implements CatalogLoader {

    /**
    * RegEx to capture the question block.
    * <p>
    * Captures three groups:
    * <p>
    *  1. Group: Contains the question<br>
    *  2. Group (optional): Timeout<br>
    *  3. Group: Answer block (all possible answers)<br>
    */
    private static final String QUESTION_BLOCK_REGEX =
        "(.+)\n(?:TIMEOUT: ([0-9]+)\n)??((?:[+-] .+\n){4}?)";
    /**
     * RegEx captures the individual answers in the captured answer block
     * from the more general expression above.
     * <p>
     * There are two capture groups:
     * <p>
     *  1. Group: +/-, which states if the answer is true or false<br>
     *  2. Group: Contains the answer<br>
     */
    private static final String ANSWER_REGEX = "([+-]) (.+)\n";

    private final Pattern blockPattern = Pattern.compile(QUESTION_BLOCK_REGEX);
    private final Pattern questionPattern = Pattern.compile(ANSWER_REGEX);

    private File[] catalogDir;
    private final Map<String, Catalog> catalogs =
        new HashMap<String, Catalog>();
    private final String location;

    public FilesystemLoader(String location) {
        this.location = location;
    }

    @Override
    public Map<String, Catalog> getCatalogs() throws LoaderException {

        if (!catalogs.isEmpty()) {
            return catalogs;
        }

        // Construct URL for package location
//        URL url = this.getClass().getClassLoader().getResource(location);
//        URL url;
//        File file = new File(location);
//        System.out.println("file list " +file.list());
       URI loc = new File(location).toURI();
       Path path = Paths.get(loc);
//      path.toString();
      System.out.println("Path "+path.toString());
//        path.ge
//		try {
//			url = new URL(location).toURI().toURL();
//			System.out.println("URL = " + url);
//		} catch (MalformedURLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
       
        
//        String url2 = "/WebContetn/WEB-INF/";
      File dir = path.toFile();
      System.out.println("FILEDIR" + dir);
//        try {
//            // Make sure the Java package exists
//            if (url != null) {
//                dir = new File(url.toURI());
//                System.out.println("DIR: " + dir);
//            } else {
//                dir = new File("/");
//            }
//        } catch (URISyntaxException e) {
//            // Try to load from the root of the classpath
//            dir = new File("/");
//        }

        // Add catalog files
        if (dir.exists() && dir.isDirectory()) {
            this.catalogDir = dir.listFiles(new CatalogFilter());
            System.out.println("Catalog dir " + catalogDir);
            for (File f : catalogDir) {
                try {
					catalogs.put(f.getName(),
					    new Catalog(f.getName(), new QuestionFileLoader(f)));
				} catch (JDOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }

        return catalogs;
    }

    @Override
    public Catalog getCatalogByName(String name) throws LoaderException {
        if (catalogs.isEmpty()) {
            getCatalogs();
        }

        return this.catalogs.get(name);
    }

    /**
     * Filter class for selecting only files with a .cat extension.
     *
     * @author Simon Westphahl
     *
     */
    private class CatalogFilter implements FileFilter {

        /**
         * Accepts only files with a .cat extension.
         */
        @Override
        public boolean accept(File pathname) {
            if (pathname.isFile() && pathname.getName().endsWith(".xml"))
                return true;
            else
                return false;
        }

    }

    private class QuestionFileLoader implements QuestionLoader {

//        private final File catalogFile;
        private String filename;
        private final List <Question> questions = new ArrayList<Question>();
        private org.jdom2.Document doc ;
        private Element Fragenkatalog;
        private Element Frage;
        private Question question;
        
        public QuestionFileLoader(File file) throws JDOMException, IOException {
//            catalogFile = file;
            filename= file.getAbsolutePath();
        
			doc = new SAXBuilder().build(filename);
			System.out.println("QuestionLoader läuft");
			
        }
        @Override
        public List<Question> getQuestions(Catalog catalog)
            throws LoaderException {
        	System.out.println("----------GETQUESTIONS------"+catalog.getName());
        	
            if (!questions.isEmpty()) {
            	System.out.println("antworten schon befüllt");
                return questions;
            }
            System.out.println("Katalog-Name"+catalog.getName());
//            System.out.println("ROOT-ELEMENT:"+doc.getRootElement());
            Fragenkatalog = doc.getRootElement();
            List <Element> Fragen = Fragenkatalog.getChildren();
            System.out.println("SizeOfCatalog"+Fragen.size());
            for(int i=0;i<Fragen.size();i++){
            	
            	Frage=Fragen.get(i);
            	question = new Question(Frage.getChildText("Fragetext"));
            	System.out.println("Fragetext"+Frage.getChildText("Fragetext"));
            	question.setTimeout(Integer.parseInt(Frage.getAttributeValue("timeout")));
            	List<Element> answers = Frage.getChildren("Antwort");
            	System.out.println("Antworten:"+answers.get(0).getText()+" "+answers.get(1).getText()+" "+answers.get(2).getText()+" "+answers.get(3).getText());
            
            	question.addAnswer(answers.get(0).getText());
            	question.addBogusAnswer(answers.get(1).getText());
            	question.addBogusAnswer(answers.get(2).getText());
            	question.addBogusAnswer(answers.get(3).getText());
            	
            	if(question.isComplete()){
            		System.out.println("Qestions ist Complete");
            		question.shuffleAnswers();
            		questions.add(question);
            		System.out.println("After ADD");
            	}
            }

            return questions;
        }

    }
}
