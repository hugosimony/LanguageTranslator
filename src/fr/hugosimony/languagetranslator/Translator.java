package fr.hugosimony.languagetranslator;

import java.awt.Dimension;

import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//import t2s.son.LecteurTexte;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
// tts
// import com.sun.speech.freetts.Voice;
// import com.sun.speech.freetts.VoiceManager;
import javax.swing.SwingConstants;

public class Translator extends JFrame {
	private static final long serialVersionUID = 1L;
	
	//**************************************************************************
	// Translator settings
	
	private Translator tl;
	
	//**************************************************************************
	// Components and settings
	
	private JPanel mainPanel = new JPanel();
	private JTextArea inputArea = new JTextArea();
	private JTextArea outputArea = new JTextArea();
	private JComboBox<String> inputLanguages = new JComboBox<>();
	private JComboBox<String> outputLanguages = new JComboBox<>();
	private JButton switchButton = new JButton();
	private JButton translateButton = new JButton();
	//private JButton readButton = new JButton();
	private JButton clearButton = new JButton();
	private JButton nextButton = new JButton();
	private JButton previousButton = new JButton();

	private String[] resultParts;
	private String result;
	private int resultIndex;
	
	//LecteurTexte lecteur = new LecteurTexte();
	
	private int DEFAULT_WIDTH = 1000;
	private int DEFAULT_HEIGHT = 600;
	
	
	private static final String[] LANGUAGES = {
		    "Francais", "Anglais", "Allemand", "Espagnol", "Italien", "Portugais", "Neerlandais", "Polonais", "Suedois",
		    "Danois", "Finois", "Grec", "Tcheque", "Roumain", "Hongrois", "Slovaque", "Bulgare", "Slovene", "Lituanien",
		    "Letton", "Estonien", "Maltais"
		  };
	
	//**************************************************************************
	
	public Translator() {
		
		/*
		 * Create the frame of the translator.
		 * Contains the input and output areas and all the buttons to translate, switch, etc...
		 */
		
		//**************************************************************************
		// Initializing frame
		
		setTitle("Language Translator");
		setLayout(null);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setMinimumSize(new Dimension(560,300));
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
		
		result = "";
		resultIndex = 0;
		
		//**************************************************************************
		// Creating components
		
		updateComponents();
		
		//**********************
		// Text Areas

		inputArea.setMargin(new Insets(10, 10, 10, 10));

		outputArea.setMargin(new Insets(10, 10, 10, 10));
		outputArea.setEditable(false);
		
		//**********************
		// Languages Selection
		
		inputLanguages = new JComboBox<>(LANGUAGES);
		((JLabel)inputLanguages.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		inputLanguages.setSelectedIndex(0);
		inputLanguages.setFocusable(false);
		
		outputLanguages = new JComboBox<>(LANGUAGES);
		((JLabel)outputLanguages.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		outputLanguages.setSelectedIndex(1);
		outputLanguages.setFocusable(false);

		//**********************
		// Switch Button

		switchButton.setText("Switch");
		switchButton.setFocusable(false);
		switchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				// Switch the languages
				String ltemp = (String) inputLanguages.getSelectedItem();
				inputLanguages.setSelectedItem(outputLanguages.getSelectedItem());
				outputLanguages.setSelectedItem(ltemp);
				
				//Switch the texts
				String text = inputArea.getText();
				inputArea.setText(outputArea.getText());
				outputArea.setText(text);
			}
		});
		
		//**********************
		// Translate Button

		translateButton.setText("Translate");
		translateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				translate((String) inputLanguages.getSelectedItem(), (String) outputLanguages.getSelectedItem(), inputArea.getText());
			}
		});
		
		/*
		//**********************
		// Read Button

		readButton.setEnabled(false);
		readButton.setText("Read");
		readButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				lecteur.setTexte(result);
				//lecteur.playAll();
				readEnglishText(result);
			}
		});
		*/
		
		//**********************
		// Clear Button

		clearButton.setText("Clear");
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Clear the areas
				inputArea.setText("");
				outputArea.setText("");
				// Reset the buttons
				previousButton.setEnabled(false);
				nextButton.setEnabled(false);
				//readButton.setEnabled(false);
				resultIndex = 0;
			}
		});

		//**********************
		// Previous translation Button

		previousButton.setEnabled(false);
		previousButton.setText("Previous");
		previousButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				printPreviousTranslation();
			}
		});
		
		//**********************
		// Next translation Button
		
		nextButton.setEnabled(false);
		nextButton.setText("Next");
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				printNextTranslation();
			}
		});
		
		//**************************************************************************
		// Adding components
		
		mainPanel.add(inputArea);
		mainPanel.add(outputArea);
		mainPanel.add(inputLanguages);
		mainPanel.add(outputLanguages);
		mainPanel.add(switchButton);
		mainPanel.add(translateButton);
		//mainPanel.add(readButton);
		mainPanel.add(clearButton);
		mainPanel.add(previousButton);
		mainPanel.add(nextButton);
		add(mainPanel);
		
		//**************************************************************************
		// Key listener
		
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyListener(this));
		
	}
	
	//**************************************************************************
	// Functions
	
	private void translate(String lfrom, String lto, String input) {
		
		/*
		 * Translate a word from a language to another and print the translation
		 */
		
		input = getWordToTranslate(input);
		
		// Reset the buttons
		previousButton.setEnabled(false);
		nextButton.setEnabled(false);
		//readButton.setEnabled(false);
		resultIndex = 0;
		
		// Send to the website the request of the translation and get its answer.
		try {
			String text = Web.getHTMLText(lfrom, lto, input);
			
			// Treat the answer
			
			if(text.contains("noresults wide_in_main")) {
				// No translation found
				outputArea.setText("No translation found :/");
			}
			
			else if(text.contains("Essayez avec cette orthographe")) {
				// The word is wrong but close. Print the more likely word.
				String corrected = getCorrectedWord(text.split("class=\'corrected\'")[1]);
				outputArea.setText("Try with \"" + corrected + "\".");
			}
			
			else {
				// get and print the translation if it exists
				String[] translationParts = text.split("lid=");
				if(translationParts.length > 1) {
					// A translation exists
					String translation = getTranslation(translationParts[resultIndex + 2]);
					resultParts = translationParts;
					result = translation;
					outputArea.setText(translation);
					if(translationParts.length > 3)
						nextButton.setEnabled(true);
					//readButton.setEnabled(true);
				}
				else {
					// There is no translation
					outputArea.setText("No translation found :/");
				}
			}
		} 
		catch (Exception e) {
			// The website has not answered
			e.printStackTrace();
		}
	}

	private void printPreviousTranslation() {
		
		/*
		 * Print the previous translation if it exists
		 * Disable the previous button if there is no more previous translation
		 */

		// Print the previous solution
		resultIndex--;
		result = getTranslation(resultParts[resultIndex + 2]);
		outputArea.setText(result);
		
		// Check if there is another previous solution
		if(resultIndex == 0)
			previousButton.setEnabled(false);
		nextButton.setEnabled(true);
		
	}
	
	private void printNextTranslation() {
		
		/*
		 * Print the next translation if it exists
		 * Disable the next button if there is no more next translation
		 */
		
		// Print the next solution
		resultIndex++;
		result = getTranslation(resultParts[resultIndex + 2]);
		outputArea.setText(result);
		
		// Check if there is another next solution
		if(resultIndex + 2 >= resultParts.length - 1)
			nextButton.setEnabled(false);
		previousButton.setEnabled(true);
	}
	
	
	private String getWordToTranslate(String input) {
		
		/*
		 * Return a word without other char than letters
		 */
		
		String result = "";
		
		for(int i = 0; i < input.length(); i++) {
			if(input.charAt(i) == ' ')
				result += "+";
			else if(Character.isLetter(input.charAt(i)))
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
	
	private Font getUpdatedFont(int width) {
		if(width < DEFAULT_WIDTH - 200)
			return new Font("Arial", Font.BOLD, 10);
		if(width < DEFAULT_WIDTH)
			return new Font("Arial", Font.BOLD, 15);
		if(width >= DEFAULT_WIDTH && width < DEFAULT_WIDTH + 300)
			return new Font("Arial", Font.BOLD, 20);
		return new Font("Arial", Font.BOLD, 25);
	}
	
	/*
	private void readEnglishText(String text) {

		 *
		 * Read a text in english
		 *
		
		String voiceName = "kevin16";
        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice voice = voiceManager.getVoice(voiceName);
        if (voice == null)
            System.exit(1);
        
        voice.allocate();
        voice.speak(text);
        voice.deallocate();
	}
	*/
	
	private void updateComponents() {
		
		/*
		 * Update the components size and font when the window is resized
		 */
		
		//**********************
		// Main panel
		
		mainPanel.setLayout(null);
		mainPanel.setLocation(0,0);
		mainPanel.setSize(getWidth(), getHeight());
		
		//**********************
		// Input Text Area
		
		inputArea.setLocation(getWidth()/15, getHeight()/6);
		inputArea.setSize(getWidth()/3, getHeight()/2);
		inputArea.setFont(getUpdatedFont(getWidth()));
		
		//**********************
		// Output Text Area
		
		outputArea.setLocation(getWidth() - getWidth()/12 - getWidth()/3, getHeight()/6);
		outputArea.setSize(getWidth()/3, getHeight()/2);
		outputArea.setFont(getUpdatedFont(getWidth()));
		outputArea.setMargin(new Insets(10, 10, 10, 10));
		
		//**********************
		// Input Languages
		
		inputLanguages.setLocation(getWidth()/15 + (getWidth()/3 - getWidth()/6) / 2, getHeight()/24);
		inputLanguages.setSize(getWidth()/6, getHeight()/12);
		inputLanguages.setFont(getUpdatedFont(getWidth()));
		
		//**********************
		// Output Languages
		
		outputLanguages.setLocation(getWidth() - getWidth()/12 - getWidth()/3 + (getWidth()/3 - getWidth()/6) / 2, getHeight()/24);
		outputLanguages.setSize(getWidth()/6, getHeight()/12);
		outputLanguages.setFont(getUpdatedFont(getWidth()));
		
		//**********************
		// Switch Button
		
		switchButton.setLocation(getWidth()/2 - getWidth()/11, getHeight()/24);
		switchButton.setSize(getWidth()/6, getHeight()/12);
		switchButton.setFont(getUpdatedFont(getWidth()));
		
		//**********************
		// Translate Button
		
		translateButton.setLocation(getWidth()/2 - getWidth()/11, getHeight()/2 - getHeight()/4);
		translateButton.setSize(getWidth()/6, getHeight()/8);
		translateButton.setFont(getUpdatedFont(getWidth()));
		
		/*
		//**********************
		// Read Button
		
		readButton.setLocation(getWidth()/2 - getWidth()/11, getHeight()/2 - getHeight()/16);
		readButton.setSize(getWidth()/6, getHeight()/8);
		readButton.setFont(getUpdatedFont(getWidth()));
		*/
		
		//**********************
		// Clear Button
		
		clearButton.setLocation(getWidth()/12, getHeight() - getHeight()/3  + getHeight()/18);
		clearButton.setSize(getWidth()/4 + getWidth()/20, getHeight()/8);
		clearButton.setFont(getUpdatedFont(getWidth()));
		
		//**********************
		// Previous Button
		
		previousButton.setLocation(getWidth() - getWidth()/14 - getWidth()/3, getHeight() - getHeight()/3  + getHeight()/18);
		previousButton.setSize(getWidth()/7, getHeight()/8);
		previousButton.setFont(getUpdatedFont(getWidth()));
		
		//**********************
		// Next Button
		
		nextButton.setLocation(getWidth() - getWidth()/11 - getWidth()/7, getHeight() - getHeight()/3 + getHeight()/18);
		nextButton.setSize(getWidth()/7, getHeight()/8);
		nextButton.setFont(getUpdatedFont(getWidth()));
		
	}
	
	//**************************************************************************
	// Classes
	
	private class KeyListener implements KeyEventDispatcher {
		
		/*
		 * Listener for the enter key
		 *  - Translate the input text when the user press enter
		 *  - Cancel the event.
		 *  - Do nothing if the input area is empty
		 *  - Cancel the space key
		 */
		
		private Translator tl;
		
		public KeyListener(Translator tl) {
			this.tl = tl;
		}
		
        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
        	
        	if(tl.isVisible()) {
        		if(event.getID() == KeyEvent.KEY_PRESSED) {
        			int keyCode = event.getKeyCode();
        			if(keyCode == KeyEvent.VK_ENTER) {
    					if(!inputArea.getText().equals(""))
    						translate((String) inputLanguages.getSelectedItem(), (String) outputLanguages.getSelectedItem(), inputArea.getText());
						event.consume();
        			}
        		}
        	}
        	return false;
        }
	}
}
