package fr.hugosimony.languagetranslator;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Translator extends JFrame {
	private static final long serialVersionUID = 1L;
	
	//**************************************************************************
	// Translator settings
	
	private Translator tl;
	
	//**************************************************************************
	// Components and settings
	
	private JPanel mainPanel = new JPanel();
	private JTextArea input = new JTextArea();
	private JTextArea output = new JTextArea();
	private JButton translateButton = new JButton();
	
	private static final String[] LANGUAGES = {
		    "af", "sq", "ar", "hy", "az", "eu", "be", "bn", "bs", "bg", "ca", "ceb", "ny", "zh-TW", "hr",
		    "cs", "da", "nl", "en", "eo", "et", "tl", "fi", "fr", "gl", "ka", "de", "el", "gu", "ht", "ha",
		    "iw", "hi", "hmn", "hu", "is", "ig", "id", "ga", "it", "ja", "jw", "kn", "kk", "km", "ko", "lo",
		    "la", "lv", "lt", "mk", "mg", "ms", "ml", "mt", "mi", "mr", "mn", "my", "ne", "no", "fa", "pl",
		    "pt", "ro", "ru", "sr", "st", "si", "sk", "sl", "so", "es", "su", "sw", "sv", "tg", "ta", "te",
		    "th", "tr", "uk", "ur", "uz", "vi", "cy", "yi", "yo", "zu"
		  };
	
	//**************************************************************************
	
	public Translator() {
		
		setTitle("Language Translator");
		setLayout(null);
		setSize(1000,600);
		setMinimumSize(new Dimension(500,400));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				updateComponents();
		    }
		});
		addWindowListener(new WindowAdapter() {
	    	public void windowClosed(WindowEvent e) {
	    		if(tl.isVisible()){
	    			System.exit(0);
	    		}
	    	}
		});
		
		translate("anglais", "francais", "get");
	}
	
	//**************************************************************************
	// Functions
	
	private void translate(String lfrom, String lto, String input) {
		
		/*
		 * Translate a word from a language to another and print the translation
		 */
		
		input = getWordToTranslate(input);
		
		// Send to the website the request of the translation and get its answer.
		try {
			String text = Web.getHTMLText(lfrom, lto, input);
			
			// Treat the answer
			
			if(text.contains("noresults wide_in_main")) {
				// No translation found
				System.out.println("there is no translation");
			}
			
			else if(text.contains("Essayez avec cette orthographe")) {
				// The word is wrong but close. Print the more likely word.
				String corrected = getCorrectedWord(text.split("class=\'corrected\'")[1]);
				System.out.println("The corrected word is : " + corrected);
			}
			
			else {
				// get and print the translation if it exists
				String[] translationParts = text.split("lid=");
				if(translationParts.length > 1) {
					// A translation exists
					String translation = getTranslation(translationParts[2]);
					System.out.println("The translation is : " + translation);
				}
				else {
					// There is no translation
					System.out.println("There is no translation for " + input);
				}
			}
		} 
		catch (Exception e) {
			// The website has not answered
			e.printStackTrace();
		}
		
	}
	
	private String getWordToTranslate(String input) {
		
		/*
		 * Return a word without other char than letters
		 */
		
		String result = "";
		
		for(int i = 0; i < input.length(); i++) {
			if(Character.isLetter(input.charAt(i)))
				result += input.charAt(i);
		}
		
		return result;
	}
	
	private String getTranslation(String translationPart) {
		
		/*
		 * Return the first word (the translation) before the # or the numbers and after the "'XX:" sequence
		 */
		
		String translation = "";
		int i = 4;
		
		// Remove the "'XX:" sequence
		while(i < translationPart.length() && !Character.isDigit(translationPart.charAt(i))) {
			translation += translationPart.charAt(i);
			i++;
		}
		
		// Remove the part after the translation and put an upper case in front of the word
		translation = translation.split("#")[0];
		translation = translation.replaceFirst(translation.charAt(0) + "", (translation.charAt(0) + "").toUpperCase());
		
		return translation;
	}
	
	private String getCorrectedWord(String correctedPart) {
		
		/*
		 * Return the corrected word that the user more likely wants
		 */
		
		return correctedPart.split(">")[1].split("<")[0];
	}
	
	private void updateComponents() {
		
	}
	
	
}
