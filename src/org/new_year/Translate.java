package org.new_year;



import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;


public class Translate {
    
    private static final String URL_STRING = "https://translate.google.com/m?hl=en&ie=UTF-8&prev=_m";
    		//"http://ajax.googleapis.com/ajax/services/language/translate?v=1.0&langpair=";
    private static final String TEXT_VAR = "&q=";
    private static final String ENCODING = "UTF-8";
    /**
     * Translates text from a given language to another given language using Google Translate
     *
     * @param text The String to translate.
     * @param from The language code to translate from.
     * @param to The language code to translate to.
     * @return The translated String.
     * @throws MalformedURLException
     * @throws IOException
     */
    public static String translate(String text, String from, String to) throws Exception {
            return retrieveTranslation(text, from, to);
    }

    /**
     * Forms an HTTP request and parses the response for a translation.
     *
     * @param text The String to translate.
     * @param from The language code to translate from.
     * @param to The language code to translate to.
     * @return The translated String.
     * @throws Exception
     */
    private static String retrieveTranslation(String text, String from, String to) throws Exception {
        try {
            StringBuilder url = new StringBuilder();
            url.append(URL_STRING).append("&sl=").append(from).append("&tl=").append(to);
            url.append(TEXT_VAR).append(URLEncoder.encode(text, ENCODING));

            
            String TranslatedTweet; 

            Translate_BackGround myTask = new Translate_BackGround();
    		String[] params = new String[1];
    		params[0] = url.toString();
    		myTask.execute(params);
    		try {
    			TranslatedTweet =  myTask.get();
    			return TranslatedTweet;
    		} catch (InterruptedException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    			return null;
    		} catch (ExecutionException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    			return null;
    		}
    		
            

        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Reads an InputStream and returns its contents as a String. Also effects rate control.
     * @param inputStream The InputStream to read from.
     * @return The contents of the InputStream as a String.
     * @throws Exception
     */
    

}