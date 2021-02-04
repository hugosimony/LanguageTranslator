package fr.hugosimony.languagetranslator;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Translator extends JFrame {
	private static final long serialVersionUID = 1L;
	
	//**************************************************************************
	// Translator settings
	
	private Translator tl;
	private int width;
	private int height;
	
	//**************************************************************************
	// Components and settings
	
	private JTextArea input = new JTextArea();
	private JTextArea output = new JTextArea();
	private JButton translateButton = new JButton();
	
	//**************************************************************************
	
	public JPanel mainPanel;
	
	public Translator() {
		
		setTitle("Language Translator");
		setLayout(null);
		setSize(1000,600);
		setMinimumSize(new Dimension(500,400));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
	    	public void windowClosed(WindowEvent e) {
	    		if(tl.isVisible()){
	    			System.exit(0);
	    		}
	    	}
		});
		
		try {
		     Desktop.getDesktop().browse(new URI("https://translate.google.com/?sl=fr&tl=en&text=" + input.getText() + "&op=translater"));
		} 
		catch (Exception e1) {
			
		}
	}
	
}
