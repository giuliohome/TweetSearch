package org.new_year;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class Translate_BackGround extends AsyncTask<String, Void, String> {
	private static final String ENCODING = "UTF-8";
	private static String charset;
    

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
        Log.d("OpentTweet", "Connecting to " + params[0]);
        HttpURLConnection uc = null;
        
       try {
    	   uc = (HttpURLConnection) new URL(params[0]).openConnection();
              uc.setDoInput(true);
           uc.setDoOutput(true);
                Log.d("OpentTweet", "getInputStream()");
                uc.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
                uc.setRequestProperty("Accept","*/*");
            InputStream is= uc.getInputStream();
            
            
            String contentType = uc.getContentType();
            charset = ENCODING;
            if (contentType != null) {
                int pos = contentType.indexOf("charset=");
                if (pos != -1) {
                    charset = contentType.substring(pos + "charset=".length());
                }
            } 
            
            String result = toString(is);
            
            //JSONObject json = new JSONObject(result);
            //return ((JSONObject)json.get("responseData")).getString("translatedText");
            return result;
        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { // http://java.sun.com/j2se/1.5.0/docs/guide/net/http-keepalive.html
            try {
				uc.getInputStream().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if (uc.getErrorStream() != null)
				try {
					uc.getErrorStream().close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
		return null;
	}
	
	
	private static String toString(InputStream inputStream) throws Exception {
        StringBuilder outputBuilder = new StringBuilder();
        try {
            String string;
            if (inputStream != null) {
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(inputStream,charset));
                while (null != (string = reader.readLine())) {
                    outputBuilder.append(string).append('\n');
                }
            }
        } catch (Exception ex) {
            Log.e("OpenTweet", "Error reading translation stream.", ex);
        }
        return outputBuilder.toString();
    }

}
