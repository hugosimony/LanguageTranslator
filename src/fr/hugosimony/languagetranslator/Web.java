package fr.hugosimony.languagetranslator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Web {

	public static String getHTMLText(String lfrom, String lto, String word) throws IOException {
		
		// Get the good url
		URL url = new URL("https://www.linguee.fr/" + lfrom + "-" + lto + "/search?source=auto&query=" + word);
		
		// Connect to the website
		URLConnection con = url.openConnection();
		InputStream input = con.getInputStream();
		
		String text = "";
		
		// Save the html in the "text" variable
		try {
			byte[] buffer = new byte[8192];
			int len;
			while ((len = input.read(buffer)) > 0)
				text += new String(buffer, 0, len);
		} finally {
			input.close();
		}
		
		return text;
	}
	
}
